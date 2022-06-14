package managedBean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;

import com.google.gson.Gson;

import dao.DaoUsuario;
import model.UsuarioPessoa;

@ViewScoped
@ManagedBean(name = "usuarioPessoaManagedBean")
public class UsuarioPessoaManagedBean {

	private UsuarioPessoa usuarioPessoa = new UsuarioPessoa();
	private List<UsuarioPessoa> list = new ArrayList<UsuarioPessoa>();
	private DaoUsuario<UsuarioPessoa> daoGenerico = new DaoUsuario<UsuarioPessoa>();
	
	@PostConstruct
	public void init() {
		list = daoGenerico.listar(UsuarioPessoa.class);
	}
	
	
	public void pesquisaCep(AjaxBehaviorEvent event) {
		
		try {
	
			URL url = new URL("https://viacep.com.br/ws/"+usuarioPessoa.getCep()+"/json/");
			URLConnection connection = url.openConnection();
			InputStream is = connection.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));
			
			String cep = "";
			StringBuilder jsonCep = new StringBuilder();
			
			while ((cep = br.readLine()) != null) {
				jsonCep.append(cep);
			}
			UsuarioPessoa viaCep = new Gson().fromJson(jsonCep.toString(), UsuarioPessoa.class);
			
			usuarioPessoa.setCep(viaCep.getCep());
			usuarioPessoa.setLogradouro(viaCep.getLogradouro());
			usuarioPessoa.setComplemento(viaCep.getComplemento());
			usuarioPessoa.setBairro(viaCep.getBairro());
			usuarioPessoa.setLocalidade(viaCep.getLocalidade());
			usuarioPessoa.setUf(viaCep.getUf());
			usuarioPessoa.setDdd(viaCep.getDdd());
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public UsuarioPessoa getUsuarioPessoa() {
		return usuarioPessoa;
	}

	public void setUsuarioPessoa(UsuarioPessoa usuarioPessoa) {
		this.usuarioPessoa = usuarioPessoa;
	}
	
	public String salvar() {
		daoGenerico.salvar(usuarioPessoa);	
		list.add(usuarioPessoa);
		usuarioPessoa = new UsuarioPessoa();
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "informação: ", "Salvao com sucesso!"));
		return "";
	}
	
	public String novo() {
		usuarioPessoa = new UsuarioPessoa();
		return "";
	}
	
	public List<UsuarioPessoa> getList() {
		return list;
	}
	
	public String delete() {
		try {
			daoGenerico.removerUsuario(usuarioPessoa);
			list.remove(usuarioPessoa);
			usuarioPessoa = new UsuarioPessoa();
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "informação: ", "Deletado com sucesso!"));
			
		} catch (Exception e) {
			if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "informação: ", "Existem telefones para o usuário!"));
			}else {
				e.printStackTrace();
			}
			
			
		}
		return "";
	}
	
	
}
