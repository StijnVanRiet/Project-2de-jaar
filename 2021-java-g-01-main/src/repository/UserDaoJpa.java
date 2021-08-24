package repository;

import domain.Contract;
import domain.ContractType;
import domain.Customer;
import domain.Employee;
import domain.Ticket;
import domain.User;

import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NoResultException;

public class UserDaoJpa extends GenericDaoJpa<User> implements UserDao {

	public UserDaoJpa() {
		super(User.class);
	}

	@Override
	public User getUserByUsername(String username) throws EntityNotFoundException {
		try {
			return em.createNamedQuery("User.findByUsername", User.class).setParameter("username", username)
					.getSingleResult();
		} catch (NoResultException ex) {
			throw new EntityNotFoundException();
		}
	}

	@Override
	public List<Employee> getEmployees() throws EntityNotFoundException {
		// TODO Auto-generated method stub
		try {
			return em.createNamedQuery("User.getAllEmployees", Employee.class).getResultList();
		} catch (NoResultException ex) {
			throw new EntityNotFoundException();
		}
	}

	@Override
	public List<Customer> getCustomers() throws EntityNotFoundException {
		try {
			return em.createNamedQuery("User.getAllCustomers", Customer.class).getResultList();
		} catch (NoResultException ex) {
			throw new EntityNotFoundException();
		}
	}
	
	@Override
	public List<Ticket> getAllTickets() throws EntityNotFoundException {
		try {
			return em.createNamedQuery("Ticket.getAllTickets", Ticket.class).getResultList();
		} catch (NoResultException ex) {
			throw new EntityNotFoundException();
		}
	}

	@Override
	public List<Ticket> getTicketsByEngineerId(int engineerId) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		try {
			return em.createNamedQuery("Ticket.getTicketsByEngineerId", Ticket.class).setParameter("engineerId", engineerId)
					.getResultList();
		} catch (NoResultException ex) {
			throw new EntityNotFoundException();
		}
	}
	
	@Override
	public List<Ticket> getTicketsByCustomerId(int customerId) throws EntityNotFoundException {
		// TODO Auto-generated method stub
		try {
			return em.createNamedQuery("Ticket.getTicketsByCustomerId", Ticket.class).setParameter("customerId", customerId)
					.getResultList();
		} catch (NoResultException ex) {
			throw new EntityNotFoundException();
		}
	}

	@Override
	public List<ContractType> getAllContractTypes() throws EntityNotFoundException {
		try {
			return em.createNamedQuery("ContractType.getAllContractTypes", ContractType.class).getResultList();
		} catch (NoResultException ex) {
			throw new EntityNotFoundException();
		}
	}
	

}
