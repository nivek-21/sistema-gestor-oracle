package Controllers;

import Helpers.Emergent;
import Models.InfoModel;
import Views.InfoView;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class InfoController implements ActionListener {

    private InfoModel model;
    private InfoView view;

    Emergent em = new Emergent();

    public InfoController() {
        model = new InfoModel();
        view = new InfoView(this, model);
    }

    public void init() {
        em.open(view, 1000, 580);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getClose()) {
            em.close();
        }

        if (e.getSource() == view.getInfo_instance()) {
            model.makeQuery("InfoInstance");
        }

        if (e.getSource() == view.getDb_name()) {
            model.makeQuery("DbName");
        }

        if (e.getSource() == view.getDb_parameters()) {
            model.makeQuery("DbParameters");
        }

        if (e.getSource() == view.getDb_version()) {
            model.makeQuery("DbVersion");
        }

        if (e.getSource() == view.getSp_path()) {
            model.makeQuery("SpPath");
        }

        if (e.getSource() == view.getCurrent_connections()) {
            model.makeQuery("CurrentConnections");
        }

        if (e.getSource() == view.getTemp_files()) {
            model.makeQuery("TempFiles");
        }
    }
}
