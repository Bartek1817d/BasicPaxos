package app.implementations;

import java.io.IOException;
import java.util.HashSet;

import app.interfaces.Acceptor;
import app.interfaces.Messenger;
import app.utils.Message;
import app.utils.ProposalID;

public class AcceptorImpl implements Acceptor {

	private ProposalID promisedID;
	private ProposalID acceptedID;
	private Object acceptedValue;
	private String acceptorUID;
	private HashSet<String> learnerUIDs;
	private Messenger messenger;

	public AcceptorImpl(String acceptorUID, HashSet<String> learnerUIDs, Messenger messenger) {
		this.acceptorUID = acceptorUID;
		this.learnerUIDs = learnerUIDs;
		this.messenger = messenger;
	}

	@Override
	public void receivePrepare(String fromUID, ProposalID proposalID) throws IOException {

		if (this.promisedID == null || proposalID.isGreaterThan(promisedID)) {
			promisedID = proposalID;
			Message message = new Message();
			message.setType("promise");
			message.setNodeUID(acceptorUID);
			message.setProposalID(proposalID);
			message.setAcceptedID(acceptedID);
			message.setAcceptedValue(acceptedValue);
			try {
				messenger.respond(fromUID, message);
			} catch (IOException e) {
				System.err.println(fromUID + " jest nieaktywny");
			}
		}
	}

	@Override
	public void receiveAcceptRequest(String fromUID, ProposalID proposalID, Object value) throws IOException {
		if (promisedID == null || proposalID.isGreaterThan(promisedID) || proposalID.equals(promisedID)) {
			promisedID = proposalID;
			acceptedID = proposalID;
			acceptedValue = value;

			Message message = new Message();
			message.setType("accepted");
			message.setNodeUID(acceptorUID);
			message.setAcceptedID(acceptedID);
			message.setAcceptedValue(acceptedValue);
			for (String learnerUID : learnerUIDs) {
				try {
				messenger.send(learnerUID, message);
				} catch(IOException e) {
					System.err.println(learnerUID + " jest nieaktywny");
				}
			}

			acceptedValue = null;
		}
	}

	public ProposalID getPromisedID() {
		return promisedID;
	}

	public ProposalID getAcceptedID() {
		return acceptedID;
	}

	public Object getAcceptedValue() {
		return acceptedValue;
	}

}
