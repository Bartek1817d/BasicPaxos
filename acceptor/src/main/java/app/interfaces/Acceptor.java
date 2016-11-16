package app.interfaces;

import app.implementations.ProposalID;

public interface Acceptor {
	public void receivePrepare(String fromUID, ProposalID proposalID);

	public void receiveAcceptRequest(String fromUID, ProposalID proposalID, Object value);
}
