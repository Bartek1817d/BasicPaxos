package app.utils;

public class Message {
	private String type = null;
	private String nodeUID = null;
	private ProposalID proposalID = null;
	private ProposalID prevAcceptedID = null;
	private Object prevAcceptedValue = null;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNodeUID() {
		return nodeUID;
	}
	public void setNodeUID(String nodeUID) {
		this.nodeUID = nodeUID;
	}
	public ProposalID getProposalID() {
		return proposalID;
	}
	public void setProposalID(ProposalID proposalID) {
		this.proposalID = proposalID;
	}
	public ProposalID getPrevAcceptedID() {
		return prevAcceptedID;
	}
	public void setPrevAcceptedID(ProposalID prevAcceptedID) {
		this.prevAcceptedID = prevAcceptedID;
	}
	public Object getPrevAcceptedValue() {
		return prevAcceptedValue;
	}
	public void setPrevAcceptedValue(Object prevAcceptedValue) {
		this.prevAcceptedValue = prevAcceptedValue;
	}
	
	
}
