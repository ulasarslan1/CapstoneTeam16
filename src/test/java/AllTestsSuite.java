
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectPackages({
    "ChargingStation",     
    "Exceptions",          
    "StorageManagement",   
    "TaskManagement",
    "OrderManagement"
})
public class AllTestsSuite {
}
