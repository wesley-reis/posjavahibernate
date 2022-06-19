package managedBean;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import dao.DaoTelefones;
import dao.DaoUsuario;
import model.Telefone;
import model.UsuarioPessoa;


@ViewScoped
@ManagedBean(name = "telefoneManageBean")
public class TelefoneManageBean {
	
	private UsuarioPessoa user = new UsuarioPessoa();
	private DaoUsuario<UsuarioPessoa> daoUsuario = new DaoUsuario<UsuarioPessoa>();
	private DaoTelefones<Telefone> daoTelefone = new DaoTelefones<Telefone>();
	
	private Telefone telefone = new Telefone();
	

	@PostConstruct
	public void init() {
		String coduser = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("codigouser");
		if(coduser != null) {			
			user = daoUsuario.pesquisar(Long.parseLong(coduser), UsuarioPessoa.class);
		}
	}
	
	public String salvar() {
		telefone.setUsuarioPessoa(user);
		daoTelefone.salvar(telefone);
		telefone = new Telefone();
		user = daoUsuario.pesquisar(user.getId(), UsuarioPessoa.class);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "informação: ", "Salvao com sucesso!"));
		return "";
	}
	
	public String remove() throws Exception {
		daoTelefone.deletarPorId(telefone);
		telefone = new Telefone();
		user = daoUsuario.pesquisar(user.getId(), UsuarioPessoa.class);
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "informação: ", "Telefone removido!"));
		
		return "";
	}
	
	public String novo() {
		telefone = new Telefone();
		return "";
	}
	
	public void setUser(UsuarioPessoa user) {
		this.user = user;
	}
	
	public UsuarioPessoa getUser() {
		return user;
	}
	
	public void setDaoTelefone(DaoTelefones<Telefone> daoTelefone) {
		this.daoTelefone = daoTelefone;
	}
	
	public DaoTelefones<Telefone> getDaoTelefone() {
		return daoTelefone;
	}
	
	public void setTelefone(Telefone telefone) {
		this.telefone = telefone;
	}
	
	public Telefone getTelefone() {
		return telefone;
	}
}
