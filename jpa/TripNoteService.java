package jpa;

import jakarta.persistence.EntityManager;
import java.util.List;

public class TripNoteService {

    public void addNote(int tripId, String noteText) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            TripNoteEntity note = new TripNoteEntity(tripId, noteText);
            em.persist(note);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<TripNoteEntity> getNotesByTrip(int tripId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT n FROM TripNoteEntity n WHERE n.tripId = :tripId ORDER BY n.createdAt DESC",
                    TripNoteEntity.class).setParameter("tripId", tripId).getResultList();
        } finally {
            em.close();
        }
    }

    public void deleteNote(int noteId, int tripId) {
        EntityManager em = JPAUtil.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();
            TripNoteEntity note = em.find(TripNoteEntity.class, noteId);
            if (note != null && note.getTripId() == tripId) em.remove(note);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}
