package org.treez.results.atom.contour.conrec;

public class ContourBlueprint {

	//#region ATTRIBUTES

	private double level;

	private Sequence entrySequence = null;

	private int count = 0;

	private static final double EPSILON = 1e-10;

	//#end region

	//#region CONSTRUCTORS

	public ContourBlueprint(double level) {
		this.level = level;
	}

	//#end region

	//#region METHODS

	@SuppressWarnings({
			"fallthrough",
			"checkstyle:magicnumber",
			"checkstyle:javancss",
			"checkstyle:avoidinlineconditionals",
			"checkstyle:cyclomaticcomplexity" })
	public void addSequence(Point leftPoint, Point rightPoint) {

		Sequence currentSequence = entrySequence;

		Sequence matchingSequenceForLeft = null;
		Sequence matchingSequenceForRight = null;

		boolean attachRightToHead = false;
		boolean attachLeftToHead = false;

		while (currentSequence != null) {
			if (matchingSequenceForLeft == null) {
				// no match for start yet
				if (pointsEqual(leftPoint, currentSequence.headSequence.point)) {
					matchingSequenceForLeft = currentSequence;
					attachRightToHead = true;
				} else if (pointsEqual(leftPoint, currentSequence.tailSequence.point)) {
					matchingSequenceForLeft = currentSequence;
				}
			}
			if (matchingSequenceForRight == null) {
				// no match for end yet
				if (pointsEqual(rightPoint, currentSequence.headSequence.point)) {
					matchingSequenceForRight = currentSequence;
					attachLeftToHead = true;
				} else if (pointsEqual(rightPoint, currentSequence.tailSequence.point)) {
					matchingSequenceForRight = currentSequence;
				}
			}
			// if we matched both no need to continue searching
			if (matchingSequenceForRight != null && matchingSequenceForLeft != null) {
				break;
			} else {
				currentSequence = currentSequence.nextSequence;
			}
		}

		//case selector based on which of mathcingStart and/or matchingEnd are set
		int caseSelector = ((matchingSequenceForLeft != null) ? 1 : 0) | ((matchingSequenceForRight != null) ? 2 : 0);

		switch (caseSelector) {
		case 0: // both unmatched, add points as new sequences
			addPointsAsNewSequence(leftPoint, rightPoint);
			++this.count; // not essential - tracks unmatchingEnder of unmerged sequences
			break;

		case 1: // leftPoint matched, rightPoint did not - thus rightPoint extends the sequence matchingSequenceForLeft
			extendSequenceWithPoint(rightPoint, matchingSequenceForLeft, attachRightToHead);
			break;

		case 2: // rightPoint matched, leftPoint did not - thus leftPoint extends sequence matchingSequenceForRight
			extendSequenceWithPoint(leftPoint, matchingSequenceForRight, attachLeftToHead);
			break;

		case 3: // both matched, can merge sequences
			// if the sequences are the same, do nothing, as we are simply closing this path (could set a flag)
			if (matchingSequenceForLeft == matchingSequenceForRight) {
				closeSequence(matchingSequenceForLeft);
				break;
			}
			mergeSequences(matchingSequenceForLeft, matchingSequenceForRight, attachRightToHead, attachLeftToHead);
			break;
		default:
			throw new IllegalStateException("default case should not happen");
		}
	}

	private void addPointsAsNewSequence(Point leftPoint, Point rightPoint) {

		Sequence leftSequence = new Sequence(leftPoint);
		Sequence rightSequence = new Sequence(rightPoint);

		leftSequence.nextSequence = rightSequence;
		rightSequence.previousSequence = leftSequence;

		// create connecting sequence and push onto head of entry sequence. The order
		// of items in that sequence is unimportant
		Sequence connectingSequence = new Sequence(null);
		connectingSequence.headSequence = leftSequence;
		connectingSequence.tailSequence = rightSequence;
		connectingSequence.nextSequence = entrySequence;
		connectingSequence.previousSequence = null;
		connectingSequence.isClosed = false;

		if (entrySequence != null) {
			entrySequence.previousSequence = connectingSequence;
		}
		entrySequence = connectingSequence;

	}

	private static void extendSequenceWithPoint(Point point, Sequence matchingSequenceForLeft, boolean attachToHead) {

		Sequence sequenceForPoint = new Sequence(point);

		if (attachToHead) {
			sequenceForPoint.nextSequence = matchingSequenceForLeft.headSequence;
			sequenceForPoint.previousSequence = null;
			matchingSequenceForLeft.headSequence.previousSequence = sequenceForPoint;
			matchingSequenceForLeft.headSequence = sequenceForPoint;
		} else {
			sequenceForPoint.nextSequence = null;
			sequenceForPoint.previousSequence = matchingSequenceForLeft.tailSequence;
			matchingSequenceForLeft.tailSequence.nextSequence = sequenceForPoint;
			matchingSequenceForLeft.tailSequence = sequenceForPoint;
		}
	}

	private static void closeSequence(Sequence matchingSequenceForLeft) {

		Sequence connectingSequence = new Sequence(matchingSequenceForLeft.tailSequence.point);
		connectingSequence.nextSequence = matchingSequenceForLeft.headSequence;
		connectingSequence.previousSequence = null;

		matchingSequenceForLeft.headSequence.previousSequence = connectingSequence;
		matchingSequenceForLeft.headSequence = connectingSequence;
		matchingSequenceForLeft.isClosed = true;
	}

	@SuppressWarnings({ "fallthrough", "checkstyle:magicnumber", "checkstyle:avoidinlineconditionals" })
	private void mergeSequences(
			Sequence matchingSequenceForLeft,
			Sequence matchingSequenceForRight,
			boolean attachRightToHead,
			boolean attachLeftToHead) {
		// there are 4 ways the sequence pair can be joined. The current setting of attachRightToHead and
		//attachLeftToHead will tell us which type of join is needed. For head/head and tail/tail joins
		// one sequence needs to be reversed

		int subCaseSelector = (attachRightToHead ? 1 : 0) | (attachLeftToHead ? 2 : 0);

		switch (subCaseSelector) {
		case 0: // tail-tail
			// reverse ma and append to matchingEnd
			reverseSequence(matchingSequenceForLeft);
			// fall through to head/tail case
		case 1: // head-tail
			// matchingStart is appended to matchingEnd and matchingStart discarded
			appendFirstToSecondSequence(matchingSequenceForLeft, matchingSequenceForRight);
			//discard ma sequence record
			this.removeSequence(matchingSequenceForLeft);
			break;
		case 3: // head-head
			// reverse matchingStart and append matchingEnd to it
			reverseSequence(matchingSequenceForLeft);
			// fall through to tail/head case
		case 2: // tail-head
			appendFirstToSecondSequence(matchingSequenceForRight, matchingSequenceForLeft);
			//discard matchingEnd sequence record
			this.removeSequence(matchingSequenceForRight);
			break;
		default:
			throw new IllegalStateException("default case should not happen");

		}
	}

	private static void appendFirstToSecondSequence(Sequence first, Sequence second) {

		second.tailSequence.nextSequence = first.headSequence;
		first.headSequence.previousSequence = second.tailSequence;
		second.tailSequence = first.tailSequence;
	}

	private void removeSequence(Sequence sequenceToRemove) {
		if (sequenceToRemove.previousSequence != null) {
			sequenceToRemove.previousSequence.nextSequence = sequenceToRemove.nextSequence;
		} else {
			this.entrySequence = sequenceToRemove.nextSequence;
		}

		if (sequenceToRemove.nextSequence != null) {
			sequenceToRemove.nextSequence.previousSequence = sequenceToRemove.previousSequence;
		}
		--this.count;
	}

	private static void reverseSequence(Sequence sequence) {

		Sequence headSequence = sequence.headSequence;

		while (headSequence != null) {
			Sequence nextSequence = headSequence.nextSequence;
			swapPreviousAndNext(headSequence);
			headSequence = nextSequence;
		}
		swapHeadAndTail(sequence);
	}

	private static void swapPreviousAndNext(Sequence sequence) {
		Sequence temp = sequence.nextSequence;
		sequence.nextSequence = sequence.previousSequence;
		sequence.previousSequence = temp;
	}

	private static void swapHeadAndTail(Sequence sequence) {
		Sequence temp = sequence.headSequence;
		sequence.headSequence = sequence.tailSequence;
		sequence.tailSequence = temp;
	}

	private static boolean pointsEqual(Point a, Point b) {
		double x = a.x - b.x;
		double y = a.y - b.y;
		return x * x + y * y < EPSILON;
	}

	//#end region

	//#region ACCESSORS

	public double getLevel() {
		return level;
	}

	public Sequence getSequence() {
		return entrySequence;
	}

	public int getSize() {
		return count;
	}

	//#end region

}
