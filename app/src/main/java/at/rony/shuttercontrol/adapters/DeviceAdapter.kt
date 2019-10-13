package at.rony.shuttercontrol.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import at.rony.shuttercontrol.model.ShutterModel
import at.rony.shuttercontrol.tools.Logger
import at.rony.shuttercontrol.views.ShutterView


class DeviceAdapter(private val context: Context) : BaseAdapter() {

    private val listItems = ArrayList<ShutterModel>()
    private val mInflator: LayoutInflater

    init {
        this.mInflator = LayoutInflater.from(context)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        var view: ShutterView?
        if (convertView == null) {
            view = ShutterView(context)

        } else {
            view = convertView as ShutterView
        }
        view.setButtonCodes(listItems.get(position))
        return view
    }

    fun setItems(apps: java.util.ArrayList<ShutterModel>) {

        Logger.d("DeviceAdapter" , "setItems: ${apps.size}")
        listItems.clear()
        listItems.addAll(apps)
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): ShutterModel {
        return listItems.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listItems.size
    }
}