package solutions.plural.sqlite.nacsqlite;

/**
 * Created by gabri on 24/10/2016.
 */
public class Filme {
    private int codigo;
    private int tempo;
    private String descricao;


    public Filme() {
    }

    public Filme(int codigo, int tempo, String descricao) {
        this.setCodigo(codigo);
        this.setTempo(tempo);
        this.setDescricao(descricao);
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public int getTempo() {
        return tempo;
    }

    public void setTempo(int tempo) {
        this.tempo = tempo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricaoItem() {
        return "-=> " + getDescricao() + " - " + getTempo() + " min";
    }
}
