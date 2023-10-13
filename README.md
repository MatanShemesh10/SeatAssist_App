# SeatAssist_App

**Background:**
Our product in the project is called "Seat Assist." We have chosen to create an application for managing seating in open office spaces within office companies. The application provides a flexible working solution and maximizes the utilization of office space. The application will create a platform that allows users (office employees) to access the seat reservation system based on their relevant working days from anywhere and at any time, without the need for customer service assistance. Users will be able to view available seats in the office, reserve their preferred seats, see occupied seats, view their past seat reservation history, make future reservations, edit their details, and more.

Today, in the market, there are companies that employ many workers, which requires a large office space, team assignments, and maintenance expenses. Socially, offices create distance among employees and hinder team cohesion, as they spend most of their day in closed rooms and are not exposed to what is happening around them.

In recent years, more and more companies have adopted the solution of open space offices, where individual desks replace traditional office rooms. The benefits of open space offices include saving physical space and the company's expenses, fostering connections among office workers through place rotation and collaboration during work. Additionally, open space offices provide an ideal solution for hybrid offices, allowing work from home in addition to office work.

**Description of Features (Activities):**
• Loading Page: The first screen users see is a loading page with an animated logo, which moves and rotates while changing color from black to white, creating a smooth transition effect. This page uses Threads for animation and loading, ensuring a seamless user experience.

• Login Page: After loading, users are directed to the login page. Here, users must enter their username and password. If the information doesn't match what's in the database, a notification appears via Toast.

• Registration Page: New users can access the registration page from the login page. Users need to choose a username, email, and password, which will be sent to the database for future login.

• Home Page: This is the main page after logging in. It displays a welcome message, the user's upcoming reservation (if any), and a button to watch an explanation video about the application. Users can also access other pages from here.

• Explanation Video Page: This page plays a video created with artificial intelligence to explain the application's features and capabilities. Users can return to the home page from this screen.

• Profile Page: This page displays user details, a default profile picture, username, email, and current password. Users can edit their current details, and changes are saved in real-time to the database.

• New Reservation Page: Users can reserve a seat for a specific date from this page. After choosing a date, the user can select a floor and a seat from drop-down menus. After submitting the reservation, the app checks seat availability for that specific date, and the user is redirected to the "My Reservations" page, where the new reservation will be displayed.

• My Reservations Page: This page displays the user's reservations. Users can scroll through the list of reservations, see the reservation date, floor, and seat. By selecting a reservation, users can go to the "Edit Reservation" page.

• Edit Reservation Page: Here, users can view and edit details of a selected reservation. They can change the date and view available seats for that day. Additionally, there is a cancel reservation button.

• Report Page: Users can report issues with workstations such as network problems, equipment shortages, or broken furniture through this page. Users fill in details, select the issue category, and provide additional information. The system automatically records the current date. Users are required to enter a phone number, and an SMS message is sent as confirmation of the report.

**Features and Extensions:**
• Users can only reserve one seat for a specific date. Attempting to book more will result in an appropriate notification.
• Reservations can be made up to one month in advance.
• Only available seats for a selected date are displayed when making a reservation.
• Empty forms are not allowed in the profile, report, edit reservation, registration, and login pages.
• Shared Preferences are used to store user information and reduce database queries and loading time.
• The home page displays the user's nearest reservation.
• The Firebase database contains two tables: reservations and users, each with a unique key automatically generated for each new user or reservation.
• Real-time updates are made to the information displayed.
• An artificial intelligence-generated video explains the application's features.
• The loading page includes an animation with multiple actions, utilizing Threads.
• Outdated reservations are automatically deleted every time a user logs in, refreshes the main activity, or enters the home page.
