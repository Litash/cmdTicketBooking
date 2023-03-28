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

**Buyer** – The users should be able retrieve list of available seats for a show, select 1 or more seats , buy and cancel tickets.
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

# Assumption
1. Since there is no requirement about setup duplicate show. Assuming a show can be setup only once, once setup, it cannot be changed.