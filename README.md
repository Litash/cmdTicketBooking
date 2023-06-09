# Use Case

Build a simple Java application for the use case of booking a Show. The program must take input from command line.

The program would setup available seats per show, allow buyers to select 1 or more available seats and buy/cancel tickets.

The application shall cater to the below 2 types of users & their requirements – (1) Admin and (2) Buyer
**Admin** – The users should be able to Setup and view the list of shows and seat allocations.
**Commands to be implemented for Admin :**

`Setup <Show Number> <Number of Rows> <Number of seats per row>  <Cancellation window in minutes>`  

(To setup the number of seats per show)

`View <Show Number>`  

(To display Show Number, Ticket#, Buyer Phone#, Seat Numbers allocated to the buyerClient)

**Buyer** – The users should be able to retrieve list of available seats for a show, select 1 or more seats , buy and cancel tickets.
**Commands to be implemented for Buyer :**

`Availability  <Show Number>`   

(To list all available seat numbers for a show. E,g A1, F4 etc)

`Book  <Show Number> <Phone#> <Comma separated list of seats>` 

(To book a ticket. This must generate a unique ticket # and display)

`Cancel  <Ticket#>  <Phone#>`

(To cancel a ticket. See constraints in the section below)

## Constraints:

1. Assume max seats per row is 10 and max rows are 26. Example seat number A1,  H5 etc. The “Add” command for adminClient must ensure rows cannot be added beyond the upper limit of 26.
2. After booking, User can cancel the seats within a time window of 2 minutes (configurable).   Cancellation after that is not allowed.
3. Only one booking per phone# is allowed per show.

## Assumption
- Show number is unique identifier for show, and the type of show number is string less than 20 characters (not only numbers)
- "Cancellation window in minutes" in show setup will be used to evaluate if the buyer is allowed to cancel
- Since there is no requirement about setup duplicate show. Assuming a show can be setup only once, once setup, it cannot be changed.
- A show can only be setup once, after initial setup, it cannot be changed. If an Admin user try to setup a show for the second time, an error will be shown.
- No authentication is required process for admin role user. The program allows the user to switch between Admin and Buyer roles freely (using logout command) for demo purpose.
- Only one buyer will book a show at any time.
- Phone number can be any number, there is no check on length
# How to use
Assuming you are using Windows.
## Prerequisite 
- Maven 3
- JDK 14

## Build 
under project root directory, use cmd.exe and run:
```
mvn clean install
mvn clean package
```
## Run
under project root directory, use cmd.exe and run:
```
run.bat
```

OR
```
cd target
java -jar cmdTicketBooking-1.0-SNAPSHOT.jar
```

### To run as Admin or Buyer directly when start:
```
cd target
java -jar cmdTicketBooking-1.0-SNAPSHOT.jar admin
```

OR
```
cd target
java -jar cmdTicketBooking-1.0-SNAPSHOT.jar buyer
```