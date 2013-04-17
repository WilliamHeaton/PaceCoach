package wgheaton.pacecoach;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import wgheaton.pacecoach.PC.Graph;

public class GraphViewer extends Fragment {
	int runid;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle b = getArguments();
		runid = b.getInt("runid");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		Graph g = new Graph(getActivity(),runid,PC.CELL_TYPE_SPEEDCAL,PC.UNITS_MPH);
		
		return g;
	}

}
