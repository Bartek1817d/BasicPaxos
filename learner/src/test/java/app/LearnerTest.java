package app;

import static org.junit.Assert.*;

import java.util.AbstractMap;

import org.junit.Before;
import org.junit.Test;

import app.implementations.LearnerImpl;
import app.interfaces.Learner;
import app.utils.ProposalID;

public class LearnerTest {
	
	private Learner learner;
	
	@Before
	public void setUp() {
		learner = new LearnerImpl(3);
	}

	@Test
	public void receiveAcceptedTestSingleProposal() {
		AbstractMap.SimpleEntry<String, String> pair1 = new AbstractMap.SimpleEntry<String, String>("key1", "val1");
		AbstractMap.SimpleEntry<String, String> pair2 = new AbstractMap.SimpleEntry<String, String>("key1", "val2");
		ProposalID proposal = new ProposalID(0, "proposer1");
		learner.receiveAccepted("acceptor1", proposal, pair1);
		learner.receiveAccepted("acceptor2", proposal, pair2);
		learner.receiveAccepted("acceptor3", proposal, pair2);
		assertEquals("val1", learner.get("key1"));
	}
	
	@Test
	public void receiveAcceptedTestMultipleProposals() {
		AbstractMap.SimpleEntry<String, String> pair1 = new AbstractMap.SimpleEntry<String, String>("key1", "val1");
		AbstractMap.SimpleEntry<String, String> pair2 = new AbstractMap.SimpleEntry<String, String>("key2", "val2");
		ProposalID proposal1 = new ProposalID(0, "proposer1");
		ProposalID proposal2 = new ProposalID(1, "proposer2");
		learner.receiveAccepted("acceptor1", proposal1, pair1);
		learner.receiveAccepted("acceptor2", proposal1, pair1);
		learner.receiveAccepted("acceptor3", proposal1, pair1);
		learner.receiveAccepted("acceptor1", proposal2, pair2);
		learner.receiveAccepted("acceptor2", proposal2, pair2);
		learner.receiveAccepted("acceptor3", proposal2, pair2);
		assertEquals("val1", learner.get("key1"));
		assertEquals("val2", learner.get("key2"));
	}

}
