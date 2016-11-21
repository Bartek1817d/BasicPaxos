package app.interfaces;

import app.utils.ProposalID;

public interface Learner {

	public void receiveAccepted(String fromUID, ProposalID proposalID, Object acceptedValue);

	public Object getFinalValue();

	ProposalID getFinalProposalID();
	
	public String get(String key);
}
