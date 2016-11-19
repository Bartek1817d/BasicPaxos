package app.interfaces;

public interface Proposer {

	public void setProposal(Object value);

	public boolean prepare();
	
	public void acceptRequest();

}
