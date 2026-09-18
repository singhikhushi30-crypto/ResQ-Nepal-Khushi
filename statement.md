# Problem Statement

## Scope

**ResQ Nepal — Disaster Management & Rescue Coordination System** is a desktop-based simulation that we developed to demonstrate how a central dispatch team could coordinate limited rescue resources during a large emergency.

The project focuses on the basic coordination problems that can occur when several incidents need attention at the same time. It does not attempt to reproduce a real emergency-response system. Instead, it provides a controlled environment in which different rescue situations can be entered and processed.

## Target Users

The system is designed around a few roles that could be involved in a central rescue operation:

- **Rescue Coordinators**: Receive distress information and enter rescue requests into the system.
- **Dispatchers**: Monitor pending requests and oversee the assignment of suitable rescue teams.
- **Logistics Officers**: Keep track of equipment and monitor how much shelter space is still available.

These roles are used to explain how the different parts of the application could work together in a real-world-style workflow, while the actual project remains an educational simulation.

## High-Level Features

1. **Priority-Based Dispatching**  
   Rescue requests are not processed only on a first-come, first-served basis. Each request receives a severity level — `CRITICAL`, `HIGH`, `MEDIUM`, or `LOW` — and is placed in a `PriorityQueue`. This allows higher-severity incidents to be considered first.

2. **Nearest Suitable Team Assignment**  
   Once a request is ready to be processed, the system checks the available rescue teams. It looks for a team that has the required specialization and then uses geographical distance to help identify a suitable nearby team.

3. **Resource Inventory Management**  
   Rescue equipment can be assigned to an operation when it is needed. After the operation, resources can be released back into the inventory. This helps prevent the system from allocating more equipment than is available.

4. **Shelter Tracking**  
   Each shelter has a maximum capacity. The system keeps track of the current occupancy and checks the limit before another person is admitted.

5. **Missing Persons Directory**  
   The application provides a simple record of people reported missing. Their information and discovery status can be updated as the simulated rescue process progresses.

6. **Dashboard**  
   The dashboard gives an overall view of the system, including rescue operations, critical emergencies, active teams, and shelter capacity. This makes it easier for an operator to see the current state without opening every module separately.
