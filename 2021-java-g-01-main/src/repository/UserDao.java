package repository;

import domain.ContractType;
import domain.Customer;
import domain.Employee;
import domain.Ticket;
import domain.User;

import java.util.List;

import javax.persistence.EntityNotFoundException;

public interface UserDao extends GenericDao<User>  {
        public User getUserByUsername(String username) throws EntityNotFoundException;  
        
        public List<Employee> getEmployees() throws EntityNotFoundException;
        
        public List<Customer> getCustomers() throws EntityNotFoundException;
        
        public List<Ticket> getAllTickets() throws EntityNotFoundException;
        
        public List<Ticket> getTicketsByEngineerId(int engineerId) throws EntityNotFoundException;
        
        public List<Ticket> getTicketsByCustomerId(int customerId) throws EntityNotFoundException;
        
        public List<ContractType> getAllContractTypes() throws EntityNotFoundException;
}
