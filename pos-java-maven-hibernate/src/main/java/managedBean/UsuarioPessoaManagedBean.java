package managedBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.AjaxBehaviorEvent;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.apache.tomcat.util.codec.binary.Base64;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.FilesUploadEvent;
import org.primefaces.model.charts.ChartData;
import org.primefaces.model.charts.axes.cartesian.CartesianScales;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearAxes;
import org.primefaces.model.charts.axes.cartesian.linear.CartesianLinearTicks;
import org.primefaces.model.charts.bar.BarChartDataSet;
import org.primefaces.model.charts.bar.BarChartModel;
import org.primefaces.model.charts.bar.BarChartOptions;
import org.primefaces.model.charts.optionconfig.animation.Animation;
import org.primefaces.model.charts.optionconfig.legend.Legend;
import org.primefaces.model.charts.optionconfig.legend.LegendLabel;
import org.primefaces.model.charts.optionconfig.title.Title;

import com.google.gson.Gson;

import dao.DaoEmail;
import dao.DaoUsuario;
import datatablelazy.LazyDataTableModelUserPessoa;
import model.EmailUser;
import model.UsuarioPessoa;

@ViewScoped
@ManagedBean(name = "usuarioPessoaManagedBean")
public class UsuarioPessoaManagedBean {

	private UsuarioPessoa usuarioPessoa = new UsuarioPessoa();
	/*Devolve lista paginada de usuários com lazy*/
	private LazyDataTableModelUserPessoa<UsuarioPessoa> list = new LazyDataTableModelUserPessoa<UsuarioPessoa>();
	
	/*Devolve lista completa de usuários*/
	//private List<UsuarioPessoa> list = new ArrayList<UsuarioPessoa>();
	private DaoUsuario<UsuarioPessoa> daoGenerico = new DaoUsuario<UsuarioPessoa>();
	private EmailUser emailUser = new EmailUser();
	private DaoEmail<EmailUser> daoEmail = new DaoEmail<EmailUser>();
	private String campoPesquisa;
	
	private BarChartModel barChartModel = new BarChartModel();


	@PostConstruct
	public void init() {
		//list = daoGenerico.listar(UsuarioPessoa.class);
		list.load(0, 5, null, null);
		createBarModel();

	}
	
	public void searchUser() {
		list.searchUser(campoPesquisa);
		createBarModel();
	}
	

	public void createBarModel() {
		ChartData data = new ChartData();
		BarChartDataSet barDataSet = new BarChartDataSet();
		
		List<Number> values = new ArrayList<>();
		List<String> labels = new ArrayList<>();
		List<String> bgColor = new ArrayList<>();
		List<String> borderColor = new ArrayList<>();

		
		for (UsuarioPessoa usuarioPessoa : list.list) {


			values.add(usuarioPessoa.getSalario());

			barDataSet.setLabel("Salário ");

			bgColor.add("rgba(255, 99, 132, 0.2)");
	        bgColor.add("rgba(255, 159, 64, 0.2)");
	        bgColor.add("rgba(255, 205, 86, 0.2)");
	        bgColor.add("rgba(75, 192, 192, 0.2)");
	        bgColor.add("rgba(54, 162, 235, 0.2)");
	        bgColor.add("rgba(153, 102, 255, 0.2)");
	        bgColor.add("rgba(201, 203, 207, 0.2)");
	 
			
	        borderColor.add("rgb(255, 99, 132)");
	        borderColor.add("rgb(255, 159, 64)");
	        borderColor.add("rgb(255, 205, 86)");
	        borderColor.add("rgb(75, 192, 192)");
	        borderColor.add("rgb(54, 162, 235)");
	        borderColor.add("rgb(153, 102, 255)");
	        borderColor.add("rgb(201, 203, 207)");

			labels.add(usuarioPessoa.getNome());

		}
		barDataSet.setData(values);
		data.addChartDataSet(barDataSet);
		
		barDataSet.setBackgroundColor(bgColor);
		barDataSet.setBorderColor(borderColor); barDataSet.setBorderWidth(1);
		data.setLabels(labels);
		barChartModel.setData(data);

		// Options
		BarChartOptions options = new BarChartOptions();
		CartesianScales cScales = new CartesianScales();
		CartesianLinearAxes linearAxes = new CartesianLinearAxes();
		linearAxes.setOffset(true);
		CartesianLinearTicks ticks = new CartesianLinearTicks();
		ticks.setBeginAtZero(true);
		linearAxes.setTicks(ticks);
		cScales.addYAxesData(linearAxes);
		options.setScales(cScales);

		Title title = new Title();
		title.setDisplay(true);
		title.setText("Salário dos Usuários");
		title.setFontSize(16);
		options.setTitle(title);

		Legend legend = new Legend();
		legend.setDisplay(false);
		legend.setPosition("top");
		LegendLabel legendLabels = new LegendLabel();
		legendLabels.setFontStyle("bold");
		legendLabels.setFontColor("#2980B9");
		legendLabels.setFontSize(14);
		legend.setLabels(legendLabels);
		options.setLegend(legend);

		// disable animation
		Animation animation = new Animation();
		animation.setDuration(200);
		options.setAnimation(animation);

		barChartModel.setOptions(options);
	}
	
	

	public String getCampoPesquisa() {
		return campoPesquisa;
	}


	public void setCampoPesquisa(String campoPesquisa) {
		this.campoPesquisa = campoPesquisa;
	}


	public BarChartModel getBarChartModel() {
		return barChartModel;
	}

	public void pesquisaCep(AjaxBehaviorEvent event) {

		try {

			URL url = new URL("https://viacep.com.br/ws/" + usuarioPessoa.getCep() + "/json/");
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
		list.list.add(usuarioPessoa);
		usuarioPessoa = new UsuarioPessoa();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "informação: ", "Salvao com sucesso!"));
		init();
		return "";
	}

	public void addEmail() {

		emailUser.setUsuarioPessoa(usuarioPessoa);
		emailUser = daoEmail.updateMerge(emailUser);
		usuarioPessoa.getEmails().add(emailUser);
		emailUser = new EmailUser();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "informação: ", "Salvao com sucesso!"));

	}

	public void removerEmail() throws Exception {
		String codigoemail = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap()
				.get("codigoemail");

		EmailUser remover = new EmailUser();
		remover.setId(Long.parseLong(codigoemail));
		daoEmail.deletarPorId(remover);

		/* Remover da lista sem consultar o banco de dados */
		usuarioPessoa.getEmails().remove(remover);
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "informação: ", "Removido com sucesso!"));

	}

	public String novo() {
		usuarioPessoa = new UsuarioPessoa();
		return "";
	}

	public LazyDataTableModelUserPessoa<UsuarioPessoa> getList() {
		createBarModel();
		return list;
	}

	public String delete() {
		try {
			daoGenerico.removerUsuario(usuarioPessoa);
			list.list.remove(usuarioPessoa);
			usuarioPessoa = new UsuarioPessoa();
			init();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_INFO, "informação: ", "Deletado com sucesso!"));

		} catch (Exception e) {
			if (e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
				FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
						"informação: ", "Existem telefones para o usuário!"));
			} else {
				e.printStackTrace();
			}

		}
		return "";
	}

	public EmailUser getEmailUser() {
		return emailUser;
	}

	public void setEmailUser(EmailUser emailUser) {
		this.emailUser = emailUser;
	}
	
	public void upload(FileUploadEvent image) {
		String imagem = "data:image/png;base64," + DatatypeConverter.printBase64Binary(image.getFile().getContent());
		
		usuarioPessoa.setImage(imagem);
	}
	
	public void downLoad() throws IOException {
		
		Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		
		String fileDownLoadId = params.get("fileDownLoadId");
		
		UsuarioPessoa pessoa = daoGenerico.pesquisar(Long.parseLong(fileDownLoadId), UsuarioPessoa.class);
		
		byte[] imagem = new Base64().decode(pessoa.getImage().split("\\,")[1]);
		
		HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
		
		response.addHeader("Content-Disposition", "attachment; filename="+pessoa.getNome()+".png");
		response.setContentType("application/octet-stream");
		response.setContentLength(imagem.length);
		response.getOutputStream().write(imagem);
		FacesContext.getCurrentInstance().responseComplete();
	}
}
