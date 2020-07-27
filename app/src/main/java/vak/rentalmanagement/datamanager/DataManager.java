package vak.rentalmanagement.datamanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import vak.rentalmanagement.data.Landlord;
import vak.rentalmanagement.data.RepairRequest;
import vak.rentalmanagement.data.Tenant;
import vak.rentalmanagement.data.Unit;

/**
 * Manages Landlords, Tenants, RepairRequests and Unis
 *
 * @author Vu Pham
 * @author Koenn Becker
 * @author Angela Ferro Capera
 * @version 1.0
 */
public class DataManager {

    private static DataManager instance;
    private static DataHandler dh;
    private Landlord landlord;
    private List<Tenant> tenantList;
    private List<RepairRequest> repairRequestList;
    private List<Unit> unitList;


    private DataManager() {
        landlord = null;
        tenantList = new ArrayList<>();
        unitList = new ArrayList<>();
        repairRequestList = new ArrayList<>();

    }

    /**
     * Set the current Landlord.
     * @param landlord the new landlord
     */
    public void setLandord(Landlord landlord) {
        this.landlord = landlord;
    }

    /**
     *  Replaces a Landlord when the user changes something, then pushes those changes to DB.
     * @param landlord the new landlord
     */
    public void replaceLandord(Landlord landlord) {
        this.landlord = landlord;
        dh.addLandlord(landlord);
    }

    /**
     * Get the current Landlord.
     *
     * @return the current Landlord.
     */
    public Landlord getLandlord() {
        return landlord;
    }

    /**
     * Singleton implementation - returns the single instance of
     * the DataManager class.
     */
    public static DataManager getInstance() {
        // DataManager.userId = userId;
        if (instance == null) {
            instance = new DataManager();
        }
        dh = new DataHandler();
        return instance;
    }

    public List<Unit> getUnitList(){
        Collections.sort(unitList);
        return unitList;
    }

    public void setUnitList(List<Unit> list) {
        unitList = list;
    }

    /**
     * Finds one unit. Returns null if there is no Unit with a matching ID.
     *
     * @param unitId the unit's ID
     * @return the Unit object, null if not found.
     */
    public Unit getUnit(String unitId) {
        Unit result = null;
        for (int i = 0; i < unitList.size(); i++) {
            if (unitList.get(i).getUnitId().equals( unitId)) {
                result =  unitList.get(i);
            }
        }
        return result;

    }

    /**
     * Replaces the unit in the DataManager's list when Firebase updates the Unit.
     *
     * @param unit the new Unit.
     */
    public void setUnit(Unit unit) {
        for (int i = 0; i < unitList.size(); i++) {
            if (unitList.get(i).getUnitId().equals(unit.getUnitId())) {
                unitList.set(i, unit);
                return;
            }
        }
        unitList.add(unit);

    }

    /**
     * Replaces a Unit in the list when the user changes something, then pushes those changes to DB.
     *
     * @param unit the new Unit
     */
    public void replaceUnit(Unit unit) {
        for (int i = 0; i < unitList.size(); i++) {
            if (unitList.get(i).getUnitId().equals(unit.getUnitId())) {
                unitList.set(i, unit);
            }
        }
        dh.addUnit(unit);
    }

    /**
     * Adds a new Unit to the DataManager list and updates Firebase.
     * If the new unit is null, it returns false.
     *
     * @param newUnit the unit to add
     */
    public void addUnit(Unit newUnit) {
        if (newUnit == null) {
            return;
        }
        landlord.getUnitIds().add(newUnit.getUnitId());
        unitList.add(newUnit);
        dh.addLandlord(landlord);
        dh.addUnit(newUnit);
        // TODO: Update Firebase
    }

    /**
     * Removes a unit from the DataManager list and updates Firebase.
     * If there is no unit with a matching id, this method returns false.
     * @param id the id of the unit to remove
     */
    public void removeUnit(String id) {
        int index = -1;
        for (int i = 0; i < unitList.size(); i++) {
            if (unitList.get(i).getUnitId().equals(id)) {
                index = i;
                break;
            }
        }
        if (index >= 0) {
            Unit unit = unitList.get(index);
            landlord.getUnitIds().remove(unit.getUnitId());
            if (unit.getTenantId() != null) {
                removeTenantFromUnit(unit.getTenantId(), unit.getUnitId());
            }
            unitList.remove(index);
            dh.deleteUnit(unit);
            dh.removeUnitPhoto(unit);
            dh.addLandlord(landlord);

        }
        // If not found
    }


    /**
     * Finds the unit belonging to a particular tenant.
     * If no unit is found, null is returned.
     *
     * @param tenantId the tenant's id
     * @return the unit belonging to the tenant, null if not found.
     */
    public Unit getTenantUnit(String tenantId) {
        for (Unit unit : unitList) {
            if (unit.getTenantId() != null) {
                if (unit.getTenantId().equals(tenantId)) {
                    return unit;
                }
            }
        }
        return null;
    }

    /**
     * Returns a sorted list of all tenants.
     *
     * @return List<Tenant> - the list of tenants
     */
    public List<Tenant> getTenantList() {
        Collections.sort(tenantList);
        return tenantList;
    }

    /**
     * Replaces the current tenant list
     * @param list the new tenant list
     */
    public void setTenantList(List<Tenant> list) {
        this.tenantList = list;
    }

    /**
     * Returns one tenant if the id exists.
     * @param id the id of the tenant to return
     * @return the tenant if found, null if not
     */
    public Tenant getTenant(String id) {
        Tenant result = null;
        for (int i = 0; i < tenantList.size(); i++) {
            if (tenantList.get(i).getTenantId().equals(id)) {
                result = tenantList.get(i);
            }
        }
        // Null if not found
        return result;
    }

    /**
     * Replaces the tenant in the DataManager's list when Firebase updates the tenant.
     *
     * @param tenant the new tenant.
     */
    public boolean setTenant(Tenant tenant) {
        for (int i = 0; i < tenantList.size(); i++) {
            if (tenantList.get(i).getTenantId().equals(tenant.getTenantId())) {
                tenantList.set(i, tenant);
                return true;
            }
        }
        return false;
    }

    /**
     * Adds a tenant in the DataManager's list
     *
     * @param tenant the new tenant.
     */
    public void addTenant(Tenant tenant) {
        tenantList.add(tenant);
    }

    /**
     * Replaces a Unit in the list when the user changes something, then pushes those changes to DB.
     *
     * @param tenant the new tenant
     */
    public void replaceTenant(Tenant tenant) {
        for (int i = 0; i < tenantList.size(); i++) {
            if (tenantList.get(i).getTenantId().equals(tenant.getTenantId())) {
                tenantList.set(i, tenant);
                dh.updateTenant(tenant);
                return;
            }
        }
    }

    /**
     * Adds a tenant's id to a unit if the tenant and unit exist.
     *
     * @param tenantId the tenant's id
     * @param unitId the unit's id
     */
    public void addTenantToUnit(String tenantId, String unitId) {
        Unit unit = getUnit(unitId);
        Tenant tenant = getTenant(tenantId);
        if (tenant != null && unit != null) {
            unit.setTenantId(tenantId);
            tenant.setTenantLandlordId(unit.getLandlordId());
            replaceTenant(tenant);
            replaceUnit(unit);
        }
    }

    /**
     * Removes a tenant's id from a unit if the tenant and unit exist.
     *
     * @param tenantId the tenant's id
     * @param unitId the unit's id
     */
    public void removeTenantFromUnit(String tenantId, String unitId) {
        // TODO: Add unitId to Tenant
        Unit unit = getUnit(unitId);
        Tenant tenant = getTenant(tenantId);
        if (tenant != null && unit != null) {
            unit.setTenantId(null);
            for (int i = 0; i < repairRequestList.size(); i++) {

                if (repairRequestList.get(i).getFromId().equals(tenantId)) {
                    unit.getRequestIds().remove(repairRequestList.get(i).getRequestId());
                    dh.removeRequest(repairRequestList.get(i));
                    repairRequestList.remove(i);
                    i--;

                }
            }
            tenant.setTenantLandlordId(null);
            replaceTenant(tenant);
            replaceUnit(unit);
            tenantList.remove(tenant);
        }
    }

    /**
     * Returns a sorted list of all repair requests
     * @return the sorted list of repair requests.
     */
    public List<RepairRequest> getRepairRequestList() {
        Collections.sort(repairRequestList);
        return this.repairRequestList;
    }

    /**
     * Sets the repair request list.
     * @param list the new list of repair requests.
     */
    public void setRepairRequestList(List<RepairRequest> list) {
        this.repairRequestList = list;
    }

    /**
     * Removes a repair request and deletes it from firebase. It also removes the repair request ID from the unit.
     * @param id the repair request ID to remove.
     */
    public void removeRepairRequest(String id) {
        for (int i = 0; i < repairRequestList.size(); i++ ) {
            if (repairRequestList.get(i).getRequestId().equals(id)) {
                RepairRequest request = repairRequestList.get(i);
                Unit unit = getTenantUnit(request.getFromId());
                unit.getRequestIds().remove(request.getRequestId());
                repairRequestList.remove(i);
                dh.removeRequest(request);
                return;
            }
        }
    }

    /**
     * Adds a new repair  request to the list and uploads it to Firestore.
     * @param newRequest the repair request to add.
     */
    public void addRepairRequest(RepairRequest newRequest) {
        if (newRequest != null) {
            newRequest.setRequestId(dh.getRepairRequestId());
            Unit unit = getTenantUnit(newRequest.getFromId());
            unit.getRequestIds().add(newRequest.getRequestId());
            replaceUnit(unit);
            dh.addRepairRequest(newRequest);
            this.repairRequestList.add(newRequest);
        }
    }

    /**
     * Gets a repair request with a matching id.
     * @param id the id of the repair request to get.
     * @return the repair request with matching id
     */
    public RepairRequest getRepairRequest(String id) {
        for (RepairRequest request : repairRequestList) {
            if (request.getRequestId().equals(id)) {
                return request;
            }
        }
        return null;
    }

    /**
     * Sets a repair request if it exists in the data manager and adds it if not.
     * @param request the request to set
     */
    public void setRepairRequest(RepairRequest request) {
        for (int i = 0; i < repairRequestList.size(); i++) {
            if (repairRequestList.get(i).getRequestId().equals(request.getRequestId())) {
                repairRequestList.set(i, request);
                return;
            }
        }
        repairRequestList.add(request);
    }

    /**
     * Replaces a repair request and changes the repair request in Firestore
     * @param request the repair request to change.
     */
    public void replaceRepairRequest(RepairRequest request) {
        for (int i = 0; i < repairRequestList.size(); i++) {
            if (repairRequestList.get(i).getRequestId().equals(request.getRequestId())) {
                repairRequestList.set(i, request);
                dh.addRepairRequest(request);
            }
        }
    }
}
