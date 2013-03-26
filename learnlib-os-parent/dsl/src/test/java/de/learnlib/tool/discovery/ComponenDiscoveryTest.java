
package de.learnlib.tool.discovery;


import de.learnlib.api.MembershipOracle;
import de.learnlib.components.LLComponentParameter;
import java.lang.reflect.Method;
import java.util.Map;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 *
 * @author falkhowar
 */
public class ComponenDiscoveryTest {
    
    @Test
    public void testDiscovery() {
        System.out.println("testDiscovery");
        String className = "de.learnlib.oracles.CounterOracleFactory";
        ComponentDirectory instance = new ComponentDirectory();
        instance.discoverComponents();
        boolean result = instance.registerComponent(className);        
        Assert.assertFalse(result);
        ComponentDirectory.ComponentDescriptor e = instance.getDescriptor("CounterOracle");
        Assert.assertNotNull(e);
        
        for (ComponentDirectory.ComponentDescriptor d : instance.getDescriptors(MembershipOracle.class)) {
        
            System.out.println("  Component: " + d.componentInfo.name());
            System.out.println("  Description: " + d.componentInfo.description());
            System.out.println("  Class: " + d.factory.getClass().getName());
            System.out.println("  Parameters: ");
            for (Map.Entry<LLComponentParameter,Method> p : d.parameters.entrySet()) {
                System.out.println("    " + p.getKey().name() + 
                        (p.getKey().required() ? " REQUIRED" : " OPTIONAL") +
                        " [" +  p.getValue().getParameterTypes()[0].getName() + "]    " + 
                        p.getKey().description());        
            }
        
        }
        
    }

}
