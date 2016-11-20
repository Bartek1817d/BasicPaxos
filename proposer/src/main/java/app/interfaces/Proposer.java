package app.interfaces;

import java.io.IOException;

public interface Proposer {

	public void setProposal(Object value);
	public boolean prepare() throws ClassNotFoundException, IOException;
	public Object chooseValue();
	public boolean acceptRequest() throws IOException;

}
