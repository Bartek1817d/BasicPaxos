package app.implementations;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import app.interfaces.Proposer;
import app.utils.Message;
import app.utils.ProposalID;

public class ProposerImpl implements Proposer {

	private String proposerUID;
	private final int quorumSize;
	private static final int TIMEOUT = 100;
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
	public void prepare() {
		promisesReceived.clear();

		proposalID.incrementNumber();

		Socket socket = new Socket();
		try {
			TimeUnit.SECONDS.sleep(10);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		for (String acceptorUID : acceptorsUIDs) {
			try {
				socket.connect(new InetSocketAddress(acceptorUID, PORT));
				//socket.setSoTimeout(TIMEOUT);

				// sending prepare message
				Message message = new Message();
				message.setType("prepare");
				message.setNodeUID(proposerUID);
				message.setProposalID(proposalID);

				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(message);
				out.close();

				// receiving promise message
				System.out.println("ok");
				ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
				Message promise = (Message) in.readObject();
				if (promise.getProposalID().equals(proposalID) && promise.getType() == "promise")
					promisesReceived.add(promise);

				in.close();
				socket.close();
			} catch (SocketTimeoutException exception) {
				System.err.println("Lost connection with acceptor " + acceptorUID);
			} catch (IOException e) {
				e.printStackTrace();
				acceptorsUIDs.remove(acceptorUID);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				if (promisesReceived.size() == quorumSize)
					break;
			}
		}

		chooseValue();
	}

	@Override
	public void acceptRequest() {

		if (promisesReceived.size() < quorumSize)
			return;

		Socket socket = new Socket();

		for (Message promise : promisesReceived) {
			try {
				socket.connect(new InetSocketAddress(promise.getNodeUID(), PORT));
				//socket.setSoTimeout(TIMEOUT);

				// sending accept request message
				Message message = new Message();
				message.setType("accept request");
				message.setNodeUID(proposerUID);
				message.setProposalID(proposalID);
				message.setAcceptedValue(proposedValue);

				ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
				out.writeObject(message);
				out.close();
				socket.close();
			} catch (SocketTimeoutException exception) {
				System.err.println("Lost connection with acceptor " + promise.getNodeUID());
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (promisesReceived.size() == quorumSize)
					break;
			}

		}

	}

	private void chooseValue() {
		for (Message promise : promisesReceived) {

			if (lastAcceptedID == null || promise.getAcceptedID().isGreaterThan(lastAcceptedID)) {
				lastAcceptedID = promise.getAcceptedID();

				if (promise.getAcceptedValue() != null)
					proposedValue = promise.getAcceptedValue();
			}
		}
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
