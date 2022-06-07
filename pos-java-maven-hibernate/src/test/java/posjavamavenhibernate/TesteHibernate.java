package posjavamavenhibernate;

import java.util.List;

import org.junit.Test;

import dao.DaoGenerico;
import model.Telefone;
import model.UsuarioPessoa;

public class TesteHibernate {
	
	@Test
	public void testeSalvar() {
	
		DaoGenerico<UsuarioPessoa> daoGenerico = new DaoGenerico<UsuarioPessoa>();
		
		UsuarioPessoa pessoa = new UsuarioPessoa();
		pessoa.setNome("Maria Clara");
		pessoa.setSobrenome("reis");
		pessoa.setEmail("maria@gmail.com");
		pessoa.setLogin("maria");
		pessoa.setSenha("123");
		pessoa.setGenre("M");
		pessoa.setIdade(8);
		
		daoGenerico.salvar(pessoa);
		
	}
	
	@Test
	public void testeBuscar() {
		
		DaoGenerico<UsuarioPessoa> daoGenerico = new DaoGenerico<UsuarioPessoa>();
		UsuarioPessoa pessoa = new UsuarioPessoa();
		pessoa.setId(2L);
		
		pessoa = daoGenerico.pesquisar(pessoa);
		
		System.out.println(pessoa);
	}
	
	@Test
	public void testeBuscarId() {
		DaoGenerico<UsuarioPessoa> daoGenerico = new DaoGenerico<>();
		
		UsuarioPessoa pessoa = daoGenerico.pesquisar(1L, UsuarioPessoa.class);
		
		System.out.println(pessoa);
	}
	
	@Test
	public void testUpdate() {
	
		DaoGenerico<UsuarioPessoa> daoGenerico = new DaoGenerico<UsuarioPessoa>();
		
		UsuarioPessoa pessoa = daoGenerico.pesquisar(6L, UsuarioPessoa.class);
		
		pessoa.setNome("Wesley");
		pessoa.setSobrenome("reis");
		pessoa.setEmail("wesley@gmail.com");
		pessoa.setLogin("wrr");
		pessoa.setSenha("123");
		pessoa.setGenre("M");
		
		pessoa = daoGenerico.updateMerge(pessoa);
		
		System.out.println(pessoa);
		
	}
	
	@Test
	public void testeDelete() {
		DaoGenerico<UsuarioPessoa> daoGenerico = new DaoGenerico<UsuarioPessoa>();
		
		UsuarioPessoa pessoa = daoGenerico.pesquisar(8L, UsuarioPessoa.class);
		
		daoGenerico.deletarPorId(pessoa);
		
	}
	
	@Test
	public void testeAll() {
		DaoGenerico<UsuarioPessoa> daoGenerico = new DaoGenerico<UsuarioPessoa>();
		List<UsuarioPessoa> list = daoGenerico.listar(UsuarioPessoa.class);
		
		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testeQueryList() {
		DaoGenerico<UsuarioPessoa> daoGenerico = new DaoGenerico<UsuarioPessoa>();
		
		List<UsuarioPessoa> list = daoGenerico.geEntityManager().createQuery(" from UsuarioPessoa where nome = 'Wesley'").getResultList();
		
		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testeQueryListMaxResult() {
		DaoGenerico<UsuarioPessoa> daoGenerico = new DaoGenerico<UsuarioPessoa>();
		
		List<UsuarioPessoa> list = daoGenerico.geEntityManager()
				.createQuery("from UsuarioPessoa order by nome")
				.setMaxResults(2)
				.getResultList();
		
		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
		}
	}
	
	
	@SuppressWarnings("unchecked")
	@Test
	public void testeQueryListParameter() {
		DaoGenerico<UsuarioPessoa> daoGenerico = new DaoGenerico<UsuarioPessoa>();
		
		List<UsuarioPessoa> list = daoGenerico.geEntityManager()
				.createQuery("from UsuarioPessoa where nome = :nome or sobrenome = :sobrenome")
				.setParameter("nome", "Wesley")
				.setParameter("sobrenome", "reis")
				.getResultList();
		
		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
		}
	}
	
	@Test
	public void testeQuerySomaIdade() {
		DaoGenerico<UsuarioPessoa> daoGenerico = new DaoGenerico<UsuarioPessoa>();
		
		Long somaIdade = (Long) daoGenerico.geEntityManager()
				.createQuery("select sum(u.idade) from UsuarioPessoa u ").getSingleResult();
		
		System.out.println("A soma das idades é: " + somaIdade);
	}
	
	@Test
	public void testeQueryMediaIdade() {
		DaoGenerico<UsuarioPessoa> daoGenerico = new DaoGenerico<UsuarioPessoa>();
		
		Double somaIdade = (Double) daoGenerico.geEntityManager()
				.createQuery("select avg(u.idade) from UsuarioPessoa u ").getSingleResult();
		
		System.out.println("A média da idade é: " + somaIdade);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testeNamedQuery() {
		DaoGenerico<UsuarioPessoa> daoGenerico = new DaoGenerico<UsuarioPessoa>();
		List<UsuarioPessoa> list = daoGenerico.geEntityManager().createNamedQuery("UsuarioPessoa.findAll").getResultList();
		
		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testeNamedQueryNome() {
		DaoGenerico<UsuarioPessoa> daoGenerico = new DaoGenerico<UsuarioPessoa>();
		List<UsuarioPessoa> list = daoGenerico.geEntityManager().createNamedQuery("UsuarioPessoa.findName")
				.setParameter("nome", "Maria Clara")
				.getResultList();
		
		for (UsuarioPessoa usuarioPessoa : list) {
			System.out.println(usuarioPessoa);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testeGravaTelefone() {
		DaoGenerico daoGenerico = new DaoGenerico();
		UsuarioPessoa pessoa =  (UsuarioPessoa) daoGenerico.pesquisar(13L, UsuarioPessoa.class);
		
		Telefone telefone = new Telefone();
		telefone.setTipo("Fixo");
		telefone.setNumero("3134783545");
		telefone.setUsuarioPessoa(pessoa);
		
		daoGenerico.salvar(telefone);
		
	}
	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testeConsultaTelefones() {
		DaoGenerico daoGenerico = new DaoGenerico();
		UsuarioPessoa pessoa =  (UsuarioPessoa) daoGenerico.pesquisar(13L, UsuarioPessoa.class);
		
		for (Telefone telefone : pessoa.getTelefones()) {
			System.out.println(telefone.getNumero());
			System.out.println(telefone.getTipo());
			System.out.println(telefone.getUsuarioPessoa().getNome());
			System.out.println("=========================================");
		}
		
	}

}
