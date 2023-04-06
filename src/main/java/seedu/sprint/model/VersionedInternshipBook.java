package seedu.sprint.model;

import java.util.ArrayList;
import java.util.List;

/**
 * {@code InternshipBook} that keeps track of its own history.
 * Referenced from <a href="https://github.com/se-edu/addressbook-level4">AB4</a> with slight modifications.
 *
 * @author se-edu
 */
public class VersionedInternshipBook extends InternshipBook {

    private final List<ReadOnlyInternshipBook> internshipBookStateList;
    private int currentStatePointer;

    /**
     * Constructs a new instance of VersionedInternshipBook.
     *
     * @param initialState initial state of the internship book.
     */
    public VersionedInternshipBook(ReadOnlyInternshipBook initialState) {
        super(initialState);

        internshipBookStateList = new ArrayList<>();
        internshipBookStateList.add(new InternshipBook(initialState));
        currentStatePointer = 0;
    }

    /**
     * Saves a copy of the current {@code InternshipBook} state at the end of the state list.
     * Undone states are removed from the state list.
     */
    public void commit() {
        removeStatesAfterCurrentPointer();
        internshipBookStateList.add(new InternshipBook(this));
        currentStatePointer++;
    }

    private void removeStatesAfterCurrentPointer() {
        internshipBookStateList.subList(currentStatePointer + 1,
                internshipBookStateList.size()).clear();
    }

    /**
     * Restores the internship book to its previous state.
     */
    public void undo() {
        if (!canUndo()) {
            throw new NoUndoableStateException();
        }

        currentStatePointer--;
        resetData(internshipBookStateList.get(currentStatePointer));
    }

    /**
     * Restores the internship book to its previously undone state.
     */
    public void redo() {
        if (!canRedo()) {
            throw new NoRedoableStateException();
        }

        currentStatePointer++;
        resetData(internshipBookStateList.get(currentStatePointer));
    }

    /**
     * Returns true if {@code undo()} has internship book states to undo.
     */
    public boolean canUndo() {
        return currentStatePointer > 0;
    }

    /**
     * Returns true if {@code redo()} has internship book states to redo.
     */
    public boolean canRedo() {
        return internshipBookStateList.size() > currentStatePointer + 1;
    }


    @Override
    public boolean equals(Object other) {
        // short circuit if same object
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof VersionedInternshipBook)) {
            return false;
        }

        VersionedInternshipBook otherVersionedInternshipBook = (VersionedInternshipBook) other;

        // state check
        return super.equals(otherVersionedInternshipBook)
                && currentStatePointer == otherVersionedInternshipBook.currentStatePointer;
    }

    /**
     * Thrown when trying to {@code undo()} but can't.
     */
    public static class NoUndoableStateException extends RuntimeException {
        private NoUndoableStateException() {
            super("Current state pointer at start of internshipBookState list, unable to undo.");
        }
    }

    /**
     * Thrown when trying to {@code redo()} but can't.
     */
    public static class NoRedoableStateException extends RuntimeException {
        private NoRedoableStateException() {
            super("Current state pointer at end of internshipBookState list, unable to redo.");
        }
    }
}