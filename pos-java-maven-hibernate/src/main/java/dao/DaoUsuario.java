package dao;

import model.UsuarioPessoa;

public class DaoUsuario<E> extends DaoGenerico<UsuarioPessoa>{
	
	
	public void removerUsuario(UsuarioPessoa pessoa) throws Exception {
		
		String sqlDeleteFone = "delete from telefone where usuariopessoa_id = " + pessoa.getId();
		getEntityManager().getTransaction().begin();
		getEntityManager().createNativeQuery(sqlDeleteFone).executeUpdate();
		getEntityManager().getTransaction().commit();
		
		super.deletarPorId(pessoa);
	}

}
