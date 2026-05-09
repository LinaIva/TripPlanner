package jpa;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_notes")
public class TripNoteEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "trip_id")
    private int tripId;

    @Column(name = "note_text")
    private String noteText;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public TripNoteEntity() { }

    public TripNoteEntity(int tripId, String noteText) {
        this.tripId = tripId;
        this.noteText = noteText;
        this.createdAt = LocalDateTime.now();
    }

    public int getId() {
        return id;
    }

    public int getTripId() {
        return tripId;
    }

    public String getNoteText() {
        return noteText;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}