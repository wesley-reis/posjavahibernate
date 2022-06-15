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

import dao.DaoUsuario;
import model.UsuarioPessoa;

@ViewScoped
@ManagedBean(name = "usuarioPessoaManagedBean")
public class UsuarioPessoaManagedBean {

	private UsuarioPessoa usuarioPessoa = new UsuarioPessoa();
	private List<UsuarioPessoa> list = new ArrayList<UsuarioPessoa>();
	private DaoUsuario<UsuarioPessoa> daoGenerico = new DaoUsuario<UsuarioPessoa>();
	private BarChartModel barChartModel = new BarChartModel();

	@PostConstruct
	public void init() {
		list = daoGenerico.listar(UsuarioPessoa.class);
		createBarModel();

	}
	
	public void createBarModel() {
		ChartData data = new ChartData();
		
		for (UsuarioPessoa usuarioPessoa : list) {
						
			BarChartDataSet barDataSet = new BarChartDataSet();
			barDataSet.setLabel(usuarioPessoa.getNome());
						
			List<Number> values = new ArrayList<>();
			values.add(usuarioPessoa.getSalario());
			barDataSet.setData(values);
			 
			List<String> bgColor = new ArrayList<>();
	        bgColor.add("rgba(153, 102, 255, 0.2)");
	        barDataSet.setBackgroundColor(bgColor);

	        List<String> borderColor = new ArrayList<>();
	        borderColor.add("rgb(153, 102, 255)");
	        barDataSet.setBorderColor(borderColor);
	        barDataSet.setBorderWidth(1);
	    
	        data.addChartDataSet(barDataSet);

	        List<String> labels = new ArrayList<>();
	        labels.add("Salário");
	        data.setLabels(labels);
	        
	        barChartModel.setData(data);
	        						
		}
		
		
		//Options
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
        legend.setDisplay(true);
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
		list.add(usuarioPessoa);
		usuarioPessoa = new UsuarioPessoa();
		FacesContext.getCurrentInstance().addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "informação: ", "Salvao com sucesso!"));
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

}
