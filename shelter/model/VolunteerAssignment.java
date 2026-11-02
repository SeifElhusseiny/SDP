package shelter.model;

// A volunteer assigned to a task at an event.
public class VolunteerAssignment {
    private final Volunteer volunteer;
    private final String task;

    public VolunteerAssignment(Volunteer volunteer, String task) {
        this.volunteer = volunteer;
        this.task = task;
    }

    public Volunteer getVolunteer() { return volunteer; }
    public String getTask() { return task; }

    @Override
    public String toString() {
        return volunteer.getName() + " (" + volunteer.getSkill() + ") -> " + task;
    }
}
