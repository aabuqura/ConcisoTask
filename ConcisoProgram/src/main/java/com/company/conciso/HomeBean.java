package com.company.conciso;

import com.company.conciso.model.Countries;
import com.company.conciso.redis.RedisConnection;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.event.map.GeocodeEvent;
import org.primefaces.model.map.GeocodeResult;
import redis.clients.jedis.Jedis;

@ManagedBean
@RequestScoped
public class HomeBean {

    private MapModel geoModel;
    private MapModel revGeoModel;
    private String centerGeoMap = "51.165691, 10.451526000000058";
    private String country;
    private List<Countries> countries; 
    FacesContext context = FacesContext.getCurrentInstance();

    @PostConstruct
    public void init() {
        geoModel = new DefaultMapModel();
    }

    public HomeBean() {
        countries = new ArrayList<>();
        Jedis jedis = RedisConnection.getConnection();

        List<String> list = jedis.lrange(ipAddress(), 0, 19);
        for (int i = 0; i < list.size(); i++) {
            Countries con = new Countries(list.get(i));
            countries.add(con);
        }
    }

    public void loadLocation(GeocodeEvent event) {
        List<GeocodeResult> location = event.getResults();
        String counAdr = location.get(0).getAddress();
        loadCountryData(counAdr, ipAddress());
        if (location != null && !location.isEmpty()) {
            LatLng center = location.get(0).getLatLng();
            centerGeoMap = center.getLat() + "," + center.getLng();

            for (int i = 0; i < location.size(); i++) {
                GeocodeResult result = location.get(i);
                geoModel.addOverlay(new Marker(result.getLatLng(), result.getAddress()));
            }
        }
    }

    public void loadCountryData(String coun, String ipAdr) {
        System.out.println("insert into redis: " + coun);
        Jedis jedis = RedisConnection.getConnection();
        jedis.lpush(ipAdr, coun);
        if (jedis.llen(ipAdr) > 20) {
            jedis.rpop(ipAdr);
        }
        countries = new ArrayList<>();
        List<String> list = jedis.lrange(ipAdr, 0, 19);
        for (int i = 0; i < list.size(); i++) {
            Countries con = new Countries(list.get(i));
            countries.add(con);
        }
        System.out.println("value: " + coun);

    }

    private String ipAddress() {
        HttpServletRequest request = (HttpServletRequest) context.getCurrentInstance().getExternalContext().getRequest();
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        System.out.println("ipAddress:" + ipAddress);
        return ipAddress;
    }

    public MapModel getGeoModel() {
        return geoModel;
    }

    public MapModel getRevGeoModel() {
        return revGeoModel;
    }

    public String getCenterGeoMap() {
        return centerGeoMap;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public List<Countries> getCountries() {
        return countries;
    }

    public void setCountries(List<Countries> countries) {
        this.countries = countries;
    }

}
