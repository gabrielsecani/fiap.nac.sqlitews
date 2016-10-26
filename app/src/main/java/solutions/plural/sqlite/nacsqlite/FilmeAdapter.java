package solutions.plural.sqlite.nacsqlite;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by gabri on 25/10/2016.
 */
public class FilmeAdapter extends ArrayAdapter<Filme>{

    Activity context;

    public FilmeAdapter(Activity context, ArrayList<Filme> objects) {
        super(context, R.layout.itemlist_filme, objects);
        this.context=context;
    }

    public static class ViewHolder {
        TextView txt_descricao;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        // reuse views
        if (rowView == null) {

            LayoutInflater inflater = context.getLayoutInflater();
            rowView = inflater.inflate(R.layout.itemlist_filme, null);

            // configure view holder
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.txt_descricao = (TextView) rowView.findViewById(R.id.txtDescricao);
            rowView.setTag(viewHolder);
        }


        // fill data
        ViewHolder holder = (ViewHolder) rowView.getTag();

        Filme filme = getItem(position);
        if (filme != null) {
            holder.txt_descricao.setText(filme.getDescricaoItem());
        }
        return rowView;
    }
}
