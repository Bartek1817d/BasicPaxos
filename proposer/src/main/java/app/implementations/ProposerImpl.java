package app.implementations;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import app.interfaces.Proposer;
import app.utils.Message;
import app.utils.ProposalID;

public class ProposerImpl implements Proposer {

	private String proposerUID;
	private final int quorumSize;
	private static final int TIMEOUT = 500;
	private static final int PORT = 1234;

	private ProposalID proposalID;
	private Object proposedValue = null;
	private ProposalID lastAcceptedID = null;
	private HashSet<String> acceptorsUIDs;
	private HashSet<Message> promisesReceived = new HashSet<Message>();

	public ProposerImpl(String proposerUID, HashSet<String> acceptorsUIDs, int quorumSize) {
		this.proposerUID = proposerUID;
		this.acceptorsUIDs = acceptorsUIDs;
		this.quorumSize = quorumSize;
		this.proposalID = new ProposalID(0, proposerUID);
	}

	@Override
	public void setProposal(Object value) {
		if (proposedValue == null)
			proposedValue = value;
	}

	@Override
	public boolean prepare() {
		promisesReceived.clear();

		proposalID.incrementNumber();
		List<String> shuffledList = new LinkedList<String>(acceptorsUIDs);
		Collections.shuffle(shuffledList);
		// Socket socket = new Socket();
		for (String acceptorUID : shuffledList) {
			try {
				// socket.connect(new InetSocketAddress(acceptorUID, PORT));
				// 
				Socket socket = new Socket(acceptorUID, PORT);
				socket.setSoTimeout(TIMEOUT);
				
				// sending prepare message
				Message message = new Message();
				message.setType("prepare");
				message.setNodeUID(proposerUID);
				message.setProposalID(proposalID);

				
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				System.out.println(proposerUID + " wysyła wiadomość prepare do " + acceptorUID);
				out.writeObject(message);

				// receiving promise message
				
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Message promise = (Message) in.readObject();
				if (promise.getProposalID().equals(proposalID) && promise.getType().equals("promise")) {
					System.out.println(proposerUID + " otrzymał wiadomość promise od " + acceptorUID);
					promisesReceived.add(promise);
				}

				socket.close();
			} catch (SocketTimeoutException exception) {
				System.err.println(proposerUID + " nie otrzymał wiadomości promise od " + acceptorUID);
			} catch (IOException e) {
				e.printStackTrace();
				// acceptorsUIDs.remove(acceptorUID);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (promisesReceived.size() == quorumSize)
					break;
			}
		}

		chooseValue();

		if (promisesReceived.size() < quorumSize)
			return false;
		else
			return true;
	}

	@Override
	public void acceptRequest() {

		if (promisesReceived.size() < quorumSize)
			return;

		System.out.println(proposerUID + " otrzymał " + promisesReceived.size() + " obietnic");

		for (Message promise : promisesReceived) {
			try {
				Socket socket = new Socket();
				socket.connect(new InetSocketAddress(promise.getNodeUID(), PORT));
				// socket.setSoTimeout(TIMEOUT);

				// sending accept request message
				Message message = new Message();
				message.setType("accept request");
				message.setNodeUID(proposerUID);
				message.setProposalID(proposalID);
				message.setAcceptedValue(proposedValue);

				System.out.println(proposerUID + " wysyła wiadomość accept request do " + promise.getNodeUID());
				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(message);
				socket.close();
			} catch (SocketTimeoutException exception) {
				System.err.println("Lost connection with acceptor " + promise.getNodeUID());
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}

	private void chooseValue() {
		for (Message promise : promisesReceived) {

			if (lastAcceptedID == null || (promise.getAcceptedID() != null && promise.getAcceptedID().isGreaterThan(lastAcceptedID))) {
				lastAcceptedID = promise.getAcceptedID();

				if (promise.getAcceptedValue() != null)
					proposedValue = promise.getAcceptedValue();
			}
		}
		
		if(lastAcceptedID == null)
			lastAcceptedID = proposalID;
	}

	public String getProposerUID() {
		return proposerUID;
	}

	public int getQuorumSize() {
		return quorumSize;
	}

	public ProposalID getProposalID() {
		return proposalID;
	}

	public Object getProposedValue() {
		return proposedValue;
	}

	public ProposalID getLastAcceptedID() {
		return lastAcceptedID;
	}

	public int numPromises() {
		return promisesReceived.size();
	}
}
