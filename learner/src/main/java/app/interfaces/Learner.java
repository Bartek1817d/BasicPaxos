package app.interfaces;

import app.utils.ProposalID;

public interface Learner {

	public boolean isComplete();

	public void receiveAccepted(String fromUID, ProposalID proposalID, Object acceptedValue);

	public Object getFinalValue();

	ProposalID getFinalProposalID();
}
