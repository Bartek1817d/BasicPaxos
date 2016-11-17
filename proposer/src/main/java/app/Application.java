package app;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;

import org.json.simple.parser.ParseException;

import app.implementations.ProposerImpl;
import app.interfaces.Proposer;
import app.utils.HostsFileParser;

public class Application {

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		HashSet<String> acceptorsUIDs = HostsFileParser.parse("hosts.json", "acceptors");
		int quorumSize = (int) Math.floor(acceptorsUIDs.size() / 2) + 1;
		Proposer proposer = new ProposerImpl(args[0], acceptorsUIDs, quorumSize);
		proposer.setProposal(10);
		proposer.prepare();
		proposer.acceptRequest();
	}

}
