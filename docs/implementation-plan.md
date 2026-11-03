# Paws & Whiskers Shelter System

## Swing UI implementation plan

The application will follow this flow:

```text
Application startup
        ↓
Welcome page
        ├── Login
        │     ↓
        │   Role-based dashboard
        │
        └── Sign up
              ↓
            Login
```

Each logged-in user will see a dashboard containing only the actions allowed
for their role.

## Roles and access

| Role | Meaning | Main access |
|---|---|---|
| Admin | Shelter administrator | Full system management, users, reports, lookup tables |
| Volunteer | Shelter worker | Animals, care records, events, assignments, volunteer hours |
| Adopter | Client interested in adoption | Available animals, adoption requests, profile |
| Donor | Client who supports the shelter | Donations, donation history, events, profile |

Public signup must not allow a user to create an Admin account. Volunteer
accounts should be created or approved by an Admin. Adopter and Donor accounts
may be available through normal signup.

## Phase 1: Prepare application startup

- [x] Keep JSON database loading and first-run sample data behavior.
- [x] Start the Swing application on the Swing Event Dispatch Thread.
- [x] Start with the Welcome page instead of opening an internal dashboard.
- [x] Keep the existing console UI available temporarily if needed for comparison.
- [x] Save data when the application closes.

## Phase 2: Create the application shell

- [x] Create or update `shelter/view/SwingMainFrame.java`.
- [x] Configure the application title, size, close behavior, and layout.
- [x] Add a `CardLayout` for switching between authentication and dashboards.
- [x] Add a logout action that returns to the Welcome page.
- [x] Ensure logout clears the current session.
- [x] Remove the prototype direct-dashboard startup flow.

## Phase 3: Create the Welcome page

- [x] Create `shelter/view/WelcomePanel.java`.
- [x] Display the shelter name and a short description.
- [x] Add a Login button.
- [x] Add a Sign Up button.
- [x] Add an Exit button.
- [x] Navigate to Login and Signup without creating a session.

## Phase 4: Add authentication data

- [x] Decide whether email will be the login identifier.
- [x] Add a password hash to the user account data.
- [x] Do not store plain-text passwords.
- [ ] Add any required account status information.
- [x] Update JSON save logic for authentication fields.
- [x] Update JSON load logic for authentication fields.
- [x] Preserve compatibility with existing user data that has no password hash.
- [x] Prevent duplicate email accounts.

## Phase 5: Implement authentication services

- [x] Create `shelter/service/AuthenticationService.java`.
- [x] Implement password hashing.
- [x] Implement password verification.
- [x] Implement login by email and password.
- [x] Implement client signup.
- [x] Restrict public signup roles to allowed client roles.
- [x] Add clear errors for invalid credentials.
- [x] Add clear errors for duplicate email addresses.
- [x] Create session handling for the logged-in user.
- [x] Create logout behavior that clears the session.

## Phase 6: Create the Login page

- [x] Create `shelter/view/LoginPanel.java`.
- [x] Add an email field.
- [x] Add a password field.
- [x] Add a Login button.
- [x] Add a Back button to return to the Welcome page.
- [x] Display authentication errors clearly.
- [x] Open the correct dashboard after successful login.
- [x] Do not open any dashboard when login fails.

## Phase 7: Create the Signup page

- [x] Create `shelter/view/SignupPanel.java`.
- [x] Add name, email, phone, and password fields.
- [x] Add password confirmation.
- [x] Add allowed client-role selection.
- [ ] Add notification-channel selection.
- [x] Validate required fields.
- [x] Validate email format.
- [x] Validate password rules.
- [x] Confirm that both password fields match.
- [x] Prevent public Admin signup.
- [x] Prevent public Volunteer signup unless explicitly approved by the design.
- [x] Create the user through the existing `UserFactory` where appropriate.
- [x] Save the new account.
- [x] Return the user to Login after successful signup.

## Phase 8: Implement the Admin dashboard

- [x] Keep `shelter/view/AdminDashboardPanel.java` as the Admin entry point.
- [x] Display the logged-in Admin's name and role.
- [x] Add a visible Logout action.
- [x] Add an Admin dashboard layout with one button or card per operation group.
- [x] Add the User Management panel.
- [x] Add the Animal Management panel.
- [ ] Add the Donation Management panel.
- [ ] Add the Event Management panel.
- [x] Add the Adoption Management panel.
- [ ] Add the Lookup Tables panel.
- [ ] Add the Communication panel.
- [ ] Add the Shelter Reports panel.
- [ ] Add Volunteer account creation and management.
- [x] Add Admin-only confirmation for destructive actions.
- [x] Ensure every Admin action is saved to JSON.

### Admin: User Management

- [x] View all users.
- [ ] Filter users by Admin, Volunteer, Adopter, or Donor.
- [ ] View a user by ID.
- [x] Add a user using `UserFactory`.
- [ ] Edit user profile and notification channels.
- [ ] Edit Volunteer skill, availability, and hours.
- [ ] Edit Adopter home type.
- [x] Delete a user only when donation/adoption references allow it.
- [x] Create or approve Volunteer accounts.

### Admin: Animal Management

- [x] Add an animal.
- [x] View all animals.
- [x] View available animals.
- [x] View an animal by ID.
- [x] Edit animal details, health, needs, and status.
- [x] Delete an animal only when no adoption request references it.
- [ ] Record care and resources given.
- [ ] View an animal's care history.

### Admin: Donation Management

- [ ] View all donations.
- [ ] View a donation by ID.
- [ ] View a donor's donation history.
- [ ] Make a donation on behalf of a donor.
- [ ] Support money and in-kind donations.
- [ ] Support all existing payment strategies.
- [ ] Edit donation amount and type.
- [ ] Delete a donation and update the donor total.
- [ ] Print a donation receipt.
- [ ] Generate a yearly tax document.

### Admin: Event Management

- [ ] Create an event.
- [ ] View all events.
- [ ] View an event with attendees and assignments.
- [ ] Edit an event.
- [ ] Cancel an event.
- [ ] Register users for an event.
- [ ] Assign Volunteers by skill.
- [ ] Mark attendance.
- [ ] Send event reminders.

### Admin: Adoption Management

- [x] View all adoption requests.
- [ ] Create an adoption request when needed.
- [x] Select an Adopter and an available animal.
- [x] Select adoption-package add-ons.
- [x] Approve a pending request.
- [x] Reject a pending request.
- [x] Delete an adoption request.
- [x] Preserve decorator package costs and descriptions.

### Admin: Lookup, communication, and reports

- [ ] Add, edit, and delete lookup-table values.
- [ ] Message one user.
- [ ] Message a whole user group.
- [ ] Post available animals to social media.
- [ ] Display animal statistics.
- [ ] Display adoption statistics and fees.
- [ ] Display donation totals.
- [ ] Display Volunteer hours.
- [ ] Display event attendance.
- [ ] Display care and resource totals.

## Phase 9: Implement the Volunteer dashboard

- [x] Keep `shelter/view/VolunteerDashboardPanel.java` as the Volunteer entry point.
- [x] Display the logged-in Volunteer's name and role.
- [x] Add a visible Logout action.
- [ ] View assigned events and tasks.
- [ ] View relevant animals.
- [ ] View animal care history.
- [ ] Record allowed animal-care information.
- [ ] Record Volunteer hours.
- [ ] View Volunteer skill and availability.
- [ ] Generate a Volunteer certificate.
- [ ] View shelter notifications.
- [ ] Prevent Volunteer access to Admin-only management actions.

## Phase 10: Implement the Adopter dashboard

- [x] Keep `shelter/view/AdopterDashboardPanel.java` as the Adopter entry point.
- [x] Display the logged-in Adopter's name and role.
- [x] Add a visible Logout action.
- [x] Browse available animals.
- [x] View permitted animal details.
- [x] Submit an adoption request for an available animal.
- [x] Select adoption-package add-ons.
- [x] View only the Adopter's own adoption requests.
- [x] View adoption-request status.
- [ ] Manage the Adopter's profile and notification preferences.
- [x] Prevent Adopters from approving, rejecting, or deleting requests.

## Phase 11: Implement the Donor dashboard

- [x] Keep `shelter/view/DonorDashboardPanel.java` as the Donor entry point.
- [x] Display the logged-in Donor's name and role.
- [x] Add a visible Logout action.
- [ ] Make a donation.
- [ ] Support money and in-kind donations.
- [ ] Select a payment method using the existing strategies.
- [ ] View only the Donor's own donation history.
- [ ] View and print the Donor's receipts.
- [ ] View shelter events.
- [ ] Manage the Donor's profile and notification preferences.
- [ ] Prevent Donors from editing other users' donations.

## Phase 12: Refactor controllers for Swing and authorization

- [ ] Identify controller methods that depend directly on `ConsoleView`.
- [ ] Separate input/display code from business operations.
- [ ] Add methods that receive values from Swing forms.
- [ ] Return operation results or errors to the Swing view.
- [ ] Add authorization checks based on the logged-in user's role.
- [ ] Prevent unauthorized direct controller calls.
- [ ] Keep business rules out of Swing panels.
- [ ] Preserve the existing console functionality until the Swing version is stable.
- [ ] Refresh the relevant panel after every successful operation.
- [ ] Save every successful operation to JSON.

## Phase 13: Preserve and reuse the existing patterns

- [ ] Keep model classes independent of Swing.
- [ ] Keep pattern classes independent of Swing.
- [ ] Reuse `UserFactory` for creating supported user types.
- [ ] Reuse payment strategies and their factory.
- [ ] Reuse notification strategies.
- [ ] Reuse adoption-package decorators.
- [ ] Preserve observer notifications for events and user updates.
- [ ] Preserve the Singleton model and database behavior.
- [ ] Do not add a pattern solely for authentication unless it provides a clear benefit.

## Phase 14: Validation, persistence, and errors

- [ ] Reuse validation rules from `Validator` where possible.
- [ ] Validate all authentication forms before submission.
- [ ] Validate all feature forms before changing the model.
- [ ] Display errors clearly and explicitly.
- [ ] Handle missing records explicitly.
- [ ] Handle database-save failures explicitly.
- [ ] Save after successful create, update, and delete operations.
- [ ] Verify that authentication fields persist after restart.
- [ ] Verify that logout prevents access to protected screens.

## Phase 15: Security and account rules

- [ ] Never store plain-text passwords.
- [ ] Do not expose password hashes in user tables or messages.
- [ ] Do not allow public Admin registration.
- [ ] Restrict Volunteer account creation or approval.
- [ ] Prevent users from viewing or changing another user's private data.
- [ ] Prevent clients from accessing internal shelter management actions.
- [ ] Clear session data on logout.
- [ ] Verify role permissions at the controller/service boundary.

## Phase 16: Testing checklist

- [ ] Verify startup opens the Welcome page.
- [ ] Verify Login navigation.
- [ ] Verify Signup navigation.
- [ ] Verify successful client signup.
- [ ] Verify duplicate-email rejection.
- [ ] Verify invalid-password rejection.
- [ ] Verify successful login for every supported role.
- [ ] Verify each role opens the correct dashboard.
- [ ] Verify logout returns to the Welcome page.
- [ ] Verify a logged-out user cannot open protected dashboards.
- [ ] Verify each role sees only its allowed actions.
- [ ] Verify unauthorized controller operations are rejected.
- [ ] Verify existing JSON data still loads.
- [ ] Verify new accounts persist after restarting.
- [ ] Verify factory, strategy, decorator, observer, and Singleton behavior.
- [ ] Verify model and pattern packages do not depend on Swing.
- [ ] Verify the UI remains responsive during normal operations.

## First milestone

- [ ] Application starts on the Welcome page.
- [ ] Welcome page has Login and Sign Up buttons.
- [ ] A client can create an account.
- [ ] The account is saved to JSON with a password hash.
- [ ] The client can log in.
- [ ] The client sees the correct dashboard.
- [ ] Logout returns the user to the Welcome page.

## Architecture rules

```text
Swing views
    ↓
Controllers and services
    ↓
Authorization/session checks
    ↓
Existing model and design patterns
    ↓
JsonDatabase
```

- [ ] Views collect input and display results.
- [ ] Controllers coordinate user actions.
- [ ] Services handle authentication and shared business operations.
- [ ] The model stores domain data.
- [ ] The database handles persistence.
- [ ] The existing design patterns remain reusable and independent of Swing.
