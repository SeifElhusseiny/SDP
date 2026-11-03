# PawKind Shelter System

A Java animal-shelter management application, currently being migrated from a console interface to a Swing desktop interface. It supports adoption workflows, donations, events, volunteer management, and shelter reporting, built around a set of classic object-oriented design patterns.

## Table of Contents

- [Overview](#overview)
- [Features](#features)
- [User Roles](#user-roles)
- [Architecture and Design Patterns](#architecture-and-design-patterns)
- [Getting Started](#getting-started)
- [Demo Accounts](#demo-accounts)
- [Managing Data](#managing-data)
- [Project Structure](#project-structure)
- [Roadmap](#roadmap)
- [Documentation](#documentation)

## Overview

PawKind persists all data as JSON files in the `data/` directory. The domain model, controllers, and pattern implementations are independent of Swing, so the original console interface and the new desktop interface share the same core logic.

**Application flow**

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

## Features

### Authentication

- Swing startup on the Event Dispatch Thread
- Login by email and password
- Signup for Adopter and Donor accounts
- Password hashing and verification
- Session login and logout
- Duplicate-email prevention
- Notification-channel selection during signup and Admin user creation
- Separate email, phone, and social-media notification histories
- Role-based dashboard selection
- JSON persistence for user accounts and password hashes

### Admin Dashboard

A table-based interface for:

- Viewing users and adding users through injected role-specific factories
- Searching and filtering users by role; editing user contact details, notification channels, Volunteer hours, and role-specific profile information
- Validating user email format and preventing duplicate email addresses
- Viewing and adding animals, and editing basic animal information
- Managing donations, donor histories and receipts, and annual tax statements
- Managing events, attendee registration, volunteer assignments, attendance, reminders, and cancellation
- Viewing, approving, rejecting, and deleting adoption requests
- Deleting users and animals, with reference checks
- Viewing a shelter summary report
- Saving successful changes to JSON

### Adopter Dashboard

- Browsing available animals and viewing animal details
- Submitting adoption requests with selectable adoption-package add-ons
- Viewing the logged-in adopter's own requests and their status
- Viewing the adopter profile
- Editing profile details and notification preferences
- Saving adoption requests to JSON

### Donor Dashboard

- Editing profile details and notification preferences

### Core Domain Capabilities

The original Java controllers and model support more operations than the Swing interface currently exposes. These remain available through the console controllers while the Swing panels are completed:

- Animal CRUD and care records
- User CRUD
- Volunteer hours and certificates
- Donation CRUD, receipts, tax documents, and payment strategies
- Event creation, registration, assignments, attendance, and reminders
- Adoption approval, rejection, and decorator-based packages
- Communication through notification strategies
- Notification history organized by channel
- Admin communication tools for individual messages, channel-subscriber broadcasts, and custom social-media posts delivered to subscribers
- Shelter impact reports

## User Roles

| Role | Description | Dashboard Status |
|------|-------------|------------------|
| Admin | Shelter administrator | Functional |
| Adopter | Client interested in adoption | Functional |
| Volunteer | Shelter worker | Shell with placeholder actions |
| Donor | Client who supports the shelter | Shell with placeholder actions |

> Public signup creates only **Adopter** and **Donor** accounts. Admin and Volunteer accounts are not available through public signup.

## Architecture and Design Patterns

| Pattern | Usage |
|---------|-------|
| **Singleton** | Shared `ShelterModel` and `JsonDatabase` |
| **Factory Method / dependency injection** | Role-specific user, payment, notification, and adoption add-on factories are injected into UI and persistence paths. |
| **Decorator** | Adoption-package add-ons |
| **Strategy** | Payment and notification methods |
| **Observer** | Donation, event, and adoption notifications |
| **MVC** | Model, controllers, and console/Swing views |

The model and pattern packages are kept independent of Swing.

## Getting Started

### Prerequisites

- A Java Development Kit (JDK) with `javac` on your `PATH`
- A graphical desktop environment (required for the Swing interface)

### Build and Run

From the project root:

```bash
rm -rf /tmp/sdp-classes
mkdir -p /tmp/sdp-classes
javac -d /tmp/sdp-classes $(find shelter -name "*.java" -print)
java -cp /tmp/sdp-classes shelter.Main
```

## Demo Accounts

Sample data is seeded by `SampleData` only when the database is first initialized. Afterwards, users are loaded from `data/users.json`.

The seeded password for all demo accounts is `demo1234`.

| Role | Name | Email |
|------|------|-------|
| Admin | Ahmed | `ahmed@shelter.org` |
| Donor | Seif | `seif@mail.com` |
| Volunteer | Moaz | `moaz@mail.com` |
| Adopter | Patrick | `patrick@mail.com` |

## Managing Data

### Reset to Fresh Sample Data

Back up the current data directory, then compile and run the application again. A new `data/` directory with fresh sample data is created.

```bash
mv data data-backup
```

### Restore a Backup

After closing the application:

```bash
rm -rf data
mv data-backup data
```

> **Warning:** Do not delete `data/` unless its saved users, animals, events, donations, and adoption records are no longer needed.

## Project Structure

```text
shelter/
  Main.java
  model/          Domain entities and validation
  database/       JSON persistence
  controller/     Console controllers
  service/        Authentication and session services
  patterns/
    factory/
      user/       User factories
      strategy/   Payment and notification factories
      decorator/  Adoption add-on factories
    strategy/     Payment and notification strategies
    decorator/    Adoption package decorators
    observer/     Observer and Subject interfaces
  view/           Console and Swing views

docs/
  implementation-plan.md
  Major Task SP 2026.docx
```

## Roadmap

The following Swing panels and actions are still in progress.

**Admin**

- [ ] Donation management
- [ ] Event management
- [ ] Lookup-table management
- [ ] Communication
- [ ] Full reports
- [ ] User filtering and detailed user editing

**Volunteer**

- [ ] Assigned events and animal-care actions
- [ ] Hours and certificate actions

**Donor**

- [ ] Donations and payment-method selection
- [ ] Donation history and receipts

**Adopter**

- [ ] Profile and notification-channel editing

**Shared**

- [ ] Animal care records and care history in Swing
- [ ] Controller/service-level authorization checks
- [ ] Automatic UI refresh through Observer events

## Documentation

The full implementation checklist is maintained in [`docs/implementation-plan.md`](docs/implementation-plan.md).