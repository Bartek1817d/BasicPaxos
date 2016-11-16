package app.interfaces;

import app.implementations.ProposalID;

public interface Messenger {

	public void sendPrepare(ProposalID proposalID);

	public void sendPromise(String proposerUID, ProposalID proposalID, ProposalID previousID, Object acceptedValue);

	public void sendAccept(ProposalID proposalID, Object proposalValue);

	public void sendAccepted(ProposalID proposalID, Object acceptedValue);

	public void onResolution(ProposalID proposalID, Object value);
}
