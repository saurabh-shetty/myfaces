/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package jakarta.faces.component;

import jakarta.faces.component.UIComponentBase;
import jakarta.faces.component.UIViewRoot;
import jakarta.faces.component.UIOutput;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UICommand;
import jakarta.faces.component.UIColumn;
import jakarta.faces.component.UIData;
import jakarta.faces.component.UINamingContainer;
import jakarta.faces.component.UIPanel;
import org.junit.Assert;
import org.junit.Test;


/**
 * Created by IntelliJ IDEA.
 * User: mathias
 * Date: 18.03.2007
 * Time: 01:19:19
 * To change this template use File | Settings | File Templates.
 */
public class UIComponentFindComponentTest extends AbstractComponentTest
{
    protected UIComponentBase _testImpl;
    
    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        _testImpl = new UIOutput();
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
    }

    @Test
    public void testWithNullExperession() throws Exception
    {
        try
        {
            _testImpl.findComponent(null);
            Assert.assertNull(_testImpl.findComponent(""));
            Assert.fail();
        }
        catch(NullPointerException e)
        {
            //Success
        }
        catch(Exception e)
        {
            Assert.fail();
        }
    }

    @Test
    public void testWithEmptyExperession() throws Exception
    {
        Assert.assertNull(_testImpl.findComponent(""));
    }

    @Test
    public void testRootExpression() throws Exception
    {
        String expression = ":parent";
        UIComponent root = new UIViewRoot();
        UIComponent parent = new UIPanel();
        
        root.setId("root");
        root.getChildren().add(parent);
        parent.setId("parent");
        parent.getChildren().add(_testImpl);        
        _testImpl.setId("testimpl");

        Assert.assertEquals(parent, _testImpl.findComponent(expression));
    }

    @Test
    public void testRelativeExpression() throws Exception
    {
        String expression = "testimpl";
        
        UIComponent namingContainer = new UINamingContainer();
        UIComponent parent = new UIPanel();
        
        namingContainer.setId("namingContainer");
        namingContainer.getChildren().add(parent);
        parent.setId("parent");
        parent.getChildren().add(_testImpl);
        _testImpl.setId("testimpl");
        
        Assert.assertEquals(_testImpl, _testImpl.findComponent(expression));
    }

    @Test
    public void testComplexRelativeExpression() throws Exception
    {
        String expression = "child1_1:testimpl";
        
        UIComponent namingContainer = new UINamingContainer();
        UIComponent child1_1 = new UINamingContainer();

        namingContainer.setId("namingContainer");
        namingContainer.getChildren().add(child1_1);
        child1_1.setId("child1_1");
        child1_1.getChildren().add(_testImpl);
        _testImpl.setId("testimpl");

        Assert.assertEquals(_testImpl, namingContainer.findComponent(expression));
    }

    @Test
    public void testOverriddenFindComponent() {
        UIViewRoot viewRoot = new UIViewRoot();
        UIData uiData = new UIData()
        {
            @Override
            public UIComponent findComponent(String expr)
            {
                return super.findComponent(stripRowIndex(expr));
            }

            public String stripRowIndex(String searchId) {
                if (searchId.length() > 0 && Character.isDigit(searchId.charAt(0)))
                {
                    for (int i = 1; i < searchId.length(); ++i)
                    {
                        char c = searchId.charAt(i);
                        if (c == SEPARATOR_CHAR)
                        {
                            searchId = searchId.substring(i + 1);
                            break;
                        }
                        if (!Character.isDigit(c))
                        {
                            break;
                        }
                    }
                }
                return searchId;
            }
        };
        uiData.setId("data");
        UIColumn column = new UIColumn();
        column.setId("column");
        UICommand command = new UICommand();
        command.setId("command");
        viewRoot.getChildren().add(uiData);
        uiData.getChildren().add(column);
        column.getChildren().add(command);

        Assert.assertNull(viewRoot.findComponent(":xx"));
        Assert.assertEquals(uiData, viewRoot.findComponent(":data"));
        Assert.assertEquals(column, viewRoot.findComponent(":data:column"));
        Assert.assertEquals(command, viewRoot.findComponent(":data:command"));
        Assert.assertEquals(command, viewRoot.findComponent("data:1:command"));
        Assert.assertEquals(command, viewRoot.findComponent(":data:1:command"));
    }

    @Test
    public void testXXFindComponent() {
        UIViewRoot viewRoot = new UIViewRoot();
        UIData uiData = new UIData();
        uiData.setId("x");
        UIColumn column = new UIColumn();
        column.setId("column");
        UICommand command = new UICommand();
        command.setId("x");
        viewRoot.getChildren().add(uiData);
        uiData.getChildren().add(column);
        column.getChildren().add(command);

        Assert.assertNull(viewRoot.findComponent(":xx"));
        Assert.assertNotNull(viewRoot.findComponent(":x"));
        Assert.assertNotNull(viewRoot.findComponent(":x:column"));
        Assert.assertNotNull(viewRoot.findComponent(":x:x"));
    }

    @Test
    public void testWithRelativeExpressionNamingContainer() throws Exception
    {
        String expression = "testimpl";
        
        UIComponent namingContainer = new UINamingContainer();
        UIComponent parent = new UIPanel();

        namingContainer.setId("namingContainer");
        namingContainer.getChildren().add(parent);
        parent.setId("parent");
        parent.getChildren().add(_testImpl);
        _testImpl.setId("testimpl");

        Assert.assertEquals(_testImpl, namingContainer.findComponent(expression));
    }

}
