package app.interfaces;

import java.io.IOException;

import app.utils.ProposalID;

public interface Acceptor {
	public void receivePrepare(String fromUID, ProposalID proposalID) throws IOException;

	public void receiveAcceptRequest(String fromUID, ProposalID proposalID, Object value) throws IOException;
}
