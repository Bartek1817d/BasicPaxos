package app;

import static org.junit.Assert.*;

import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import app.implementations.AcceptorImpl;
import app.interfaces.Acceptor;
import app.interfaces.Messenger;
import app.utils.HostsFileParser;
import app.utils.Message;
import app.utils.ProposalID;

import static org.mockito.Mockito.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Set;


public class AcceptorTest {

	private static Set<String> learners;
	private Messenger messenger;
	private Acceptor acceptor;

	@BeforeClass
	public static void parseLearners() throws FileNotFoundException, IOException, ParseException {
		learners = HostsFileParser.parse("src/main/resources/hosts.json", "learners");
	}

	@Before
	public void prepare() {
		messenger = mock(Messenger.class);
		acceptor = new AcceptorImpl("acceptor1", learners, messenger);
	}

	@Test
	public void discardLowerProposalsTest() throws IOException {
		ProposalID proposal = new ProposalID(0, "proposer1");
		acceptor.receivePrepare("proposer1", proposal);
		acceptor.receivePrepare("proposer1", proposal);
		verify(messenger, atMost(1)).respond(anyString(), any(Message.class));
	}
	
	@Test
	public void promiseMessageTest() throws IOException {
		ProposalID proposal = new ProposalID(0, "proposer1");
		ArgumentCaptor<String> captorDest = ArgumentCaptor.forClass(String.class);
		ArgumentCaptor<Message> captorMess = ArgumentCaptor.forClass(Message.class);
		acceptor.receivePrepare("proposer1", proposal);
		verify(messenger).respond(captorDest.capture(), captorMess.capture());
		String capturedDest = captorDest.getValue();
		Message capturedMess = captorMess.getValue();
		assertEquals("proposer1", capturedDest);
		assertEquals("promise", capturedMess.getType());
		assertNull(capturedMess.getAcceptedValue());
	}
	
	@Test
	public void discardLowerAcceptRequestsTest() throws IOException {
		ProposalID higherProposal = new ProposalID(1, "proposer1");
		ProposalID lowerProposal = new ProposalID(0, "proposer2");
		acceptor.receivePrepare("proposer1", higherProposal);
		acceptor.receiveAcceptRequest("proposer2", lowerProposal, 10);
		verify(messenger, never()).send(anyString(), any(Message.class));
	}
	
	@Test
	public void acceptRequestTest() throws IOException {
		ProposalID proposal = new ProposalID(1, "proposer1");
		acceptor.receivePrepare("proposer1", proposal);
		acceptor.receiveAcceptRequest("proposer2", proposal, 10);
		verify(messenger, times(learners.size())).send(anyString(), any(Message.class));
	}

}
