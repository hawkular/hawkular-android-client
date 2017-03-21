package org.hawkular.client.android.adapter;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.util.Log;

import org.assertj.android.api.Assertions;
import org.hawkular.client.android.backend.model.Note;
import org.hawkular.client.android.util.Randomizer;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pallavi on 21/03/17.
 */

public class NotesAdapterTester {
    private Context context;

    @Before
    public void setUp() {
        context = InstrumentationRegistry.getTargetContext();
    }

    @Test
    public void count() {
        List<Note> notes = generateNotes();

        NotesAdapter notesAdapter = new NotesAdapter(context, notes);

        Assertions.assertThat(notesAdapter).hasCount(notes.size());
    }

    @Test
    public void item() {
        Note note = generateNote();

        List<Note> notes = new ArrayList<>();
        notes.add(note);
        notes.addAll(generateNotes());

        NotesAdapter notesAdapter = new NotesAdapter(context, notes);

        for(int i=0; i<notes.size(); i++){
            Assertions.assertThat(notesAdapter).hasItem(notes.get(i), i);
        }
    }

    private List<Note> generateNotes() {
        List<Note> notes = new ArrayList<>();
        long randomSize = Randomizer.generateNumber();
        for (int notePosition = 0; notePosition < randomSize; notePosition++) {
            notes.add(generateNote());
        }

        return notes;
    }

    private Note generateNote() {
        return new Note(Randomizer.generateString(), Randomizer.generateNumber(), Randomizer.generateString());
    }
}

