
# 1. )Program Structure

  The application uses a mockdatabase the implimentation for this is in the *source* package.
  It contains a DataSource that can aggregate multiple tables. And Each table class contains records
  and a *QueryBuilder* instance. The *QueryBuilder* is used to filter data, aggregate data and group data.
  
  The next class is the *OrdersService* class which utilized all the functionality listed above to perform
  requirements in this test. 
  
  The finally we have the ConsoleInterface class that is responsable for the command line interface.

#2.) Tests
  All main components of the application have been tested. Please view this tests folder.
 
