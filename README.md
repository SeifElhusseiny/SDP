# Paws & Whiskers Shelter System

Paws & Whiskers is a Java animal-shelter management application being
migrated from a console interface to a Swing desktop interface.

## Current application flow

```text
Application startup
        ↓
Welcome page
        ├── Login
        │     ↓
        │   Role-based dashboard
        │
        └── Sign Up
              ↓
            Login
```

The application loads and saves its data as JSON files in the `data/`
directory.

## User roles

| Role | Meaning | Current dashboard |
|---|---|---|
| Admin | Shelter administrator | Functional Admin dashboard |
| Volunteer | Shelter worker | Dashboard shell with placeholder actions |
| Adopter | Client interested in adoption | Functional Adopter dashboard |
| Donor | Client who supports the shelter | Dashboard shell with placeholder actions |

Public signup currently creates only Adopter or Donor accounts. Admin and
Volunteer accounts are not available through public signup.

## Implemented functionality

### Application and authentication

- Swing startup on the Event Dispatch Thread
- Welcome page
- Login by email and password
- Signup for Adopter and Donor accounts
- Password hashing and verification
- Session login and logout
- JSON persistence for user accounts and password hashes
- Duplicate-email prevention
- Role-based dashboard selection

### Admin dashboard

The Admin dashboard currently provides a table-based interface for:

- Viewing users
- Adding users through `UserFactory`
- Viewing and adding animals
- Editing basic animal information
- Viewing donations
- Viewing events
- Viewing adoption requests
- Approving or rejecting adoption requests
- Deleting adoption requests
- Deleting users and animals with reference checks
- Viewing a shelter summary report
- Saving successful changes to JSON

### Adopter dashboard

The Adopter dashboard currently provides:

- Browsing available animals
- Viewing animal details
- Submitting adoption requests
- Selecting adoption-package add-ons
- Viewing the logged-in adopter's own requests
- Viewing request status
- Viewing the adopter profile
- Saving adoption requests to JSON

### Existing Java domain functionality

The original Java controllers and model already support more operations than
are currently exposed through Swing:

- Animal CRUD and care records
- User CRUD
- Volunteer hours and certificates
- Donation CRUD, receipts, tax documents, and payment strategies
- Event creation, registration, assignments, attendance, and reminders
- Adoption approval, rejection, and decorator-based packages
- Communication through notification strategies
- Shelter impact reports

These operations remain available in the console controllers while the Swing
panels are being completed.

## Panels still to be implemented

The following Swing panels or actions are intentionally still incomplete:

- Admin donation management
- Admin event management
- Admin lookup-table management
- Admin communication
- Admin full reports
- Animal care records and care history in Swing
- Admin user filtering and detailed user editing
- Volunteer assigned-events and animal-care actions
- Volunteer hours and certificate actions
- Donor donations and payment-method selection
- Donor donation history and receipts
- Adopter profile and notification-channel editing
- Controller/service-level authorization checks
- Automatic UI refresh through Observer events

The implementation checklist is maintained in
[`docs/implementation-plan.md`](docs/implementation-plan.md).

## Design patterns

The application uses the following patterns:

- **Singleton:** shared `ShelterModel` and `JsonDatabase`
- **Factory:** creation of user types and adoption packages
- **Decorator:** adoption-package add-ons
- **Strategy:** payment and notification methods
- **Observer:** donation, event, and adoption notifications
- **MVC:** model, controllers, and console/Swing views

The model and pattern packages are kept independent of Swing.

## Running the application

Run commands from the project root:

```bash
cd "/storage/UNIcourses/Spftwar Design Patterns/project/SDP"
rm -rf /tmp/sdp-classes
mkdir -p /tmp/sdp-classes
javac -d /tmp/sdp-classes $(find shelter -name "*.java" -print)
java -cp /tmp/sdp-classes shelter.Main
```

The application requires a graphical desktop environment.

## Demo accounts

The seeded demo password for all accounts is:

```text
demo1234
```

| Role | Name | Email |
|---|---|---|
| Admin | Ahmed | `ahmed@shelter.org` |
| Donor | Seif | `seif@mail.com` |
| Volunteer | Moaz | `moaz@mail.com` |
| Adopter | Patrick | `patrick@mail.com` |

Demo users are created by `SampleData` only when the database is initialized.
After that, users are loaded from `data/users.json`.

## Resetting demo data

To preserve the current data, rename the data directory as a backup:

```bash
cd "/storage/UNIcourses/Spftwar Design Patterns/project/SDP"
mv data data-backup
```

Then compile and run the application again. A new `data/` directory with fresh
sample data will be created.

To restore the backup after closing the application:

```bash
rm -rf data
mv data-backup data
```

Do not delete `data/` unless its saved users, animals, events, donations, and
adoption records are no longer needed.

## Project structure

```text
shelter/
  Main.java
  model/          Domain entities and validation
  database/       JSON persistence
  controller/     Existing console controllers
  service/        Authentication and session services
  patterns/       Factory, Strategy, Decorator, and Observer implementations
  view/           Console and Swing views

docs/
  implementation-plan.md
  Major Task SP 2026.docx
```
