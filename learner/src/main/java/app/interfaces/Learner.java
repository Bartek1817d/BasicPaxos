package app.interfaces;

import app.implementations.ProposalID;

public interface Learner {

	public boolean isComplete();

	public void receiveAccepted(String fromUID, ProposalID proposalID, Object acceptedValue);

	public Object getFinalValue();

	ProposalID getFinalProposalID();
}
