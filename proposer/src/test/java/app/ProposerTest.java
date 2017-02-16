package app;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import app.interfaces.Proposer;
import app.utils.HostsFileParser;
import app.utils.Message;
import app.utils.ProposalID;
import app.implementations.ProposerImpl;
import app.interfaces.Messenger;

public class ProposerTest {

	private static Set<String> acceptors;
	private static int quorumSize;
	private Messenger messenger;
	private Proposer proposer;

	@BeforeClass
	public static void parseLearners() throws FileNotFoundException, IOException, ParseException {
		acceptors = HostsFileParser.parse("src/main/resources/hosts.json", "acceptors");
		quorumSize = (int) Math.floor(acceptors.size() / 2) + 1;
	}

	@Before
	public void setUp() {
		messenger = mock(Messenger.class);
		proposer = new ProposerImpl("proposer1", acceptors, quorumSize, messenger);
	}

	@Test
	public void generalTest() throws UnknownHostException, ClassNotFoundException, IOException {
		ProposalID proposal = new ProposalID(1, "proposer1");
		List<Message> promises = new LinkedList<Message>();
		for (String acceptor : acceptors) {
			Message promise = new Message();
			promise.setType("promise");
			promise.setProposalID(proposal);
			promise.setNodeUID(acceptor);
			promises.add(promise);
		}
		Message[] promisesArray = new Message[promises.size()-1];
		promises.subList(1, promises.size()).toArray(promisesArray);
		when(messenger.sendReceive(anyString(), any(Message.class))).thenReturn(promises.get(0), promisesArray);
		assertTrue("Prepare phase failure", proposer.prepare());
		proposer.acceptRequest();
		verify(messenger, times(quorumSize)).send(anyString(), any(Message.class));
	}

}
