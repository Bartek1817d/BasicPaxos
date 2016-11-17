package app.implementations;

import java.util.HashMap;

import app.interfaces.Learner;
import app.utils.ProposalID;

public class LearnerImpl implements Learner {

	class Proposal {
		int acceptCount;
		int retentionCount;
		Object value;

		Proposal(int acceptCount, int retentionCount, Object value) {
			this.acceptCount = acceptCount;
			this.retentionCount = retentionCount;
			this.value = value;
		}
	}

	private final int quorumSize;
	private HashMap<ProposalID, Proposal> proposals = new HashMap<ProposalID, Proposal>();
	private HashMap<String, ProposalID> acceptors = new HashMap<String, ProposalID>();
	private Object finalValue = null;
	private ProposalID finalProposalID = null;

	public LearnerImpl(int quorumSize) {
		this.quorumSize = quorumSize;
	}

	@Override
	public boolean isComplete() {
		return finalValue != null;
	}

	@Override
	public void receiveAccepted(String fromUID, ProposalID proposalID, Object acceptedValue) {

		if (isComplete())
			return;

		ProposalID oldPID = acceptors.get(fromUID);

		if (oldPID != null && !proposalID.isGreaterThan(oldPID))
			return;

		acceptors.put(fromUID, proposalID);

		if (oldPID != null) {
			Proposal oldProposal = proposals.get(oldPID);
			oldProposal.retentionCount -= 1;
			if (oldProposal.retentionCount == 0)
				proposals.remove(oldPID);
		}

		if (!proposals.containsKey(proposalID))
			proposals.put(proposalID, new Proposal(0, 0, acceptedValue));

		Proposal thisProposal = proposals.get(proposalID);

		thisProposal.acceptCount += 1;
		thisProposal.retentionCount += 1;

		if (thisProposal.acceptCount == quorumSize) {
			finalProposalID = proposalID;
			finalValue = acceptedValue;
			proposals.clear();
			acceptors.clear();

			System.out.println("Final value = " + finalValue);
		}
	}

	public int getQuorumSize() {
		return quorumSize;
	}

	@Override
	public Object getFinalValue() {
		return finalValue;
	}

	@Override
	public ProposalID getFinalProposalID() {
		return finalProposalID;
	}
}
