package app.implementations;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import app.interfaces.Messenger;
import app.interfaces.Proposer;
import app.utils.Message;
import app.utils.ProposalID;

public class ProposerImpl implements Proposer {

	private final String proposerUID;
	private final int quorumSize;

	private Messenger messenger;
	private ProposalID proposalID;
	private Object proposedValue = null;
	private ProposalID lastAcceptedID = null;
	private HashSet<String> acceptorsUIDs;
	private HashSet<Message> promisesReceived = new HashSet<Message>();

	public ProposerImpl(String proposerUID, HashSet<String> acceptorsUIDs, int quorumSize, Messenger messenger) {
		this.proposerUID = proposerUID;
		this.acceptorsUIDs = acceptorsUIDs;
		this.quorumSize = quorumSize;
		this.proposalID = new ProposalID(0, proposerUID);
		this.messenger = messenger;
	}

	@Override
	public void setProposal(Object value) {
		proposedValue = value;
	}

	@Override
	public boolean prepare() throws ClassNotFoundException, IOException {
		promisesReceived.clear();

		proposalID.incrementNumber();

		List<String> shuffledList = new LinkedList<String>(acceptorsUIDs);
		Collections.shuffle(shuffledList);

		Message message = new Message();
		message.setType("prepare");
		message.setNodeUID(proposerUID);
		message.setProposalID(proposalID);

		for (String acceptorUID : shuffledList) {
			try {

				System.out.println(proposerUID + " wysyła wiadomość " + message.getType() + " do " + acceptorUID);
				Message promise = messenger.sendReceive(acceptorUID, message);

				if (promise.getProposalID().equals(proposalID) && promise.getType().equals("promise")) {
					System.out.println(proposerUID + " otrzymał wiadomość promise od " + acceptorUID);
					promisesReceived.add(promise);
				}

				if (promisesReceived.size() >= quorumSize)
					break;

			} catch (IOException exception) {
				System.err.println(acceptorUID + " jest nieaktywny");
			}
		}

		if (promisesReceived.size() < quorumSize)
			return false;
		else
			return true;
	}

	@Override
	public boolean acceptRequest() throws IOException {

		if (promisesReceived.size() < quorumSize)
			return false;

		System.out.println(proposerUID + " otrzymał " + promisesReceived.size() + " obietnic");

		Message message = new Message();
		message.setType("accept request");
		message.setNodeUID(proposerUID);
		message.setProposalID(proposalID);
		message.setAcceptedValue(proposedValue);

		for (Message promise : promisesReceived) {
			try {
				System.out.println(proposerUID + " wysyła wiadomość accept request do " + promise.getNodeUID());
				messenger.send(promise.getNodeUID(), message);
			} catch (IOException exception) {
				System.err.println(promise.getNodeUID() + " jest niekatywny");
				return false;
			}
		}
		
		return true;
	}

	public Object chooseValue() {
		for (Message promise : promisesReceived) {
			if (promise.getAcceptedID() != null
					&& (lastAcceptedID == null || promise.getAcceptedID().isGreaterThan(lastAcceptedID))) {
				lastAcceptedID = promise.getAcceptedID();

				if (promise.getAcceptedValue() != null)
					proposedValue = promise.getAcceptedValue();
			}
		}

		if (lastAcceptedID == null)
			lastAcceptedID = proposalID;

		return proposedValue;
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
