package dao;

import java.util.List;

import javax.persistence.Query;

import model.UsuarioPessoa;

public class DaoUsuario<E> extends DaoGenerico<UsuarioPessoa>{
	
	
	public void removerUsuario(UsuarioPessoa pessoa) throws Exception {
		getEntityManager().getTransaction().begin();
		
		String sqlDeleteFone = "delete from telefone where usuariopessoa_id = " + pessoa.getId();
		getEntityManager().createNativeQuery(sqlDeleteFone).executeUpdate();
		
		String sqlEmail = "delete from emailuser where usuariopessoa_id = " + pessoa.getId();
		getEntityManager().createNativeQuery(sqlEmail).executeUpdate();
		
		getEntityManager().getTransaction().commit();
		
		super.deletarPorId(pessoa);
	}


	@SuppressWarnings("unchecked")
	public List<UsuarioPessoa> searchUser(String campoPesquisa) {
		
		Query query = super.getEntityManager().createQuery("from UsuarioPessoa where lower(nome) like '%" + campoPesquisa.toLowerCase() + "%'");
				
		return query.getResultList();
	}


}
