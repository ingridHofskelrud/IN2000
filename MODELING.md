# Use case diagram

![Use case diagram](./modeling/usecase/UseCaseDiagram.svg)

# Create route 

Name: Create route 

Actor: User

Precondition: App is open on route screen 

Postcondition: User has created route 

Main flow:
1. User decides to add more stops
2. User activates searchbar
3. System displays searchbar 
4. User searches for airport
5. Airport list is updated to match search prompt
6. User selects airport
7. System adds airport to route 
8. Go back to 1


Alternative flow - User decides to not add more stops:
1. Route is created

Alternative flow - Add stop from map:
        
2. 
    1. User opens dialog for map 
    2. System displays dialog 
    3. User selects point on map
    4. User confirms point
    5. System adds point to route
    6. Go back to 1

[Sequence Diagram](./modeling/create_route/sequenceDiagram.md)

[Class Diagram](./modeling/create_route/ClassDiagramCreateRoute.md)

[Activity Diagram](./modeling/create_route/activityDiagram.md)

# Save stop as favorite
Name: Save stop as favorite

Actor: User

Precondition: User is viewing the favorite screen

Postcondition: The location is saved as favorite

Main flow 

1. User presses button to add favorite
2. The system displays a dialog to the user

Alternative flow

3. 
    1. User opens the searchbar
    2. User searches for an airport
    3. Systems displays a filtered list of airports
    4. User selects an airport
    5. Systems displays a preview of the favorite location
    6. User confirms the favorite location

Alternative flow

3. 
    1. User clicks on button for selecting favorite from map
    2. System displays a map to the user 
    3. User selects a point on the map
    4. User confirms the selected point
    3. Systems displays a preview of the favorite location and prompts the user for a name
    6. User writes name for location
    7. User confirms name for location

Main flow 

4. Location is added to favorites

[Sequence Diagram](./modeling/save_favorite/sequenceDiagram.md)

[Class Diagram](./modeling/save_favorite/classDiagram.md)

[Activity Diagram](./modeling/save_favorite/activityDiagram.md)

# Search TAF/METAR
Name: Search TAF/METAR

Actor: User

Precondition: User is viewing the taf/metar screen

Postcondition: The taf/metar is displayed to the user

Main Flow
1. User opens the searchbar
2. User searches for an airport
3. System displays a filtered list of airports
4. User selects an airport
5. System displays the TAF and METAR for the chosen airport

Alternative flow - TAFMETAR not available

5. 
    1. System displays message "no taf/metar avaliable" to the user


[Sequence Diagram](./modeling/search_tafmetar/sequenceDiagram.md)

[Class Diagram](./modeling/search_tafmetar/classDiagram.md)

[Activity Diagram](./modeling/search_tafmetar/activityDiagram.md)


# View map

Name: View map

Actor: User 

Precondition: User is on map screen

Postcondition: Map is displayed to user

Main flow: 

1. System fetches airports, favorite locations and selected route
2. Map is displayed to user with airports, favorites and route

[Sequence Diagram](./modeling/view_map/sequenceDiagram.md)

[Class Diagram](./modeling/view_map/classDiagram.md)

# View SigChart
Name: View SigChart

Actor: User

Precondition: User is on SigChartScreen

Postcondition: SigChart or error message is displayed

Main flow:

1. System fetches Bitmap from Met API
2. System displays a Bitmap to user

Alternative flow: 

1. Bitmap not available
2. Systems displays error message to user

[Sequence Diagram](./modeling/view_sigchart/sequenceDiagram.md)

[Class Diagram](./modeling/view_sigchart/classDiagram.md)
