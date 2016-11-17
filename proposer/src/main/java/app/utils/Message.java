package app.utils;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Message implements Serializable{
	private String type = null;
	private String nodeUID = null;
	private ProposalID proposalID = null;
	private ProposalID acceptedID = null;
	private Object acceptedValue = null;
	
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
	public ProposalID getAcceptedID() {
		return acceptedID;
	}
	public void setAcceptedID(ProposalID acceptedID) {
		this.acceptedID = acceptedID;
	}
	public Object getAcceptedValue() {
		return acceptedValue;
	}
	public void setAcceptedValue(Object acceptedValue) {
		this.acceptedValue = acceptedValue;
	}
	
	
}
