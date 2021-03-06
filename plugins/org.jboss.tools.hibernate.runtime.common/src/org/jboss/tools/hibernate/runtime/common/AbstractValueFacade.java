package org.jboss.tools.hibernate.runtime.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;

import org.jboss.tools.hibernate.runtime.spi.IColumn;
import org.jboss.tools.hibernate.runtime.spi.IPersistentClass;
import org.jboss.tools.hibernate.runtime.spi.IProperty;
import org.jboss.tools.hibernate.runtime.spi.ITable;
import org.jboss.tools.hibernate.runtime.spi.IType;
import org.jboss.tools.hibernate.runtime.spi.IValue;
import org.jboss.tools.hibernate.runtime.spi.IValueVisitor;

public abstract class AbstractValueFacade 
extends AbstractFacade 
implements IValue {

	private IValue collectionElement = null;
	private ITable table = null;
	private IType type = null;
	private ITable collectionTable = null;
	private HashSet<IColumn> columns = null;
	private IValue key = null;
	private IValue index = null;
	private HashSet<IProperty> properties = null;
	private IPersistentClass owner = null;
	private IPersistentClass associatedClass = null;

	public AbstractValueFacade(
			IFacadeFactory facadeFactory, 
			Object target) {
		super(facadeFactory, target);
	}

	@Override
	public boolean isSimpleValue() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isSimpleValue", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public boolean isCollection() {
		return getCollectionClass().isAssignableFrom(getTarget().getClass());
	}
	
	@Override
	public IValue getCollectionElement() {
		if (isCollection() && collectionElement == null) {
			Object targetElement = Util.invokeMethod(
					getTarget(), 
					"getElement", 
					new Class[] {}, 
					new Object[] {});
			if (targetElement != null) {
				collectionElement = getFacadeFactory().createValue(targetElement);
			}
		}
		return collectionElement;
	}

	@Override
	public boolean isOneToMany() {
		return getOneToManyClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isManyToOne() {
		return getManyToOneClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isOneToOne() {
		return getOneToOneClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isMap() {
		return getMapClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isComponent() {
		return getComponentClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isToOne() {
		return getToOneClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public Boolean isEmbedded() {
		Boolean result = null;
		if (isComponent() || isToOne()) {
			result = (Boolean)Util.invokeMethod(
					getTarget(), 
					"isEmbedded", 
					new Class[] {}, 
					new Object[] {});
		}
		return result;
	}

	@Override
	public Object accept(IValueVisitor valueVisitor) {
		return valueVisitor.accept(this);
	}

	@Override
	public ITable getTable() {
		if (table == null) {
			Object targetTable = Util.invokeMethod(
					getTarget(), 
					"getTable", 
					new Class[] {}, 
					new Object[] {});
			if (targetTable != null) {
				table = getFacadeFactory().createTable(targetTable);
			}
		}
		return table;
	}

	@Override
	public IType getType() {
		if (type == null) {
			Object targetType = Util.invokeMethod(
					getTarget(), 
					"getType", 
					new Class[] {}, 
					new Object[] {});	
			if (targetType != null) {
				type = getFacadeFactory().createType(targetType);
			}
		}
		return type;
	}

	@Override
	public void setElement(IValue element) {
		if (isCollection()) {
			Object elementTarget = Util.invokeMethod(
					element, 
					"getTarget", 
					new Class[] {}, 
					new Object[] {});
			Util.invokeMethod(
					getTarget(), 
					"setElement", 
					new Class[] { getValueClass() }, 
					new Object[] { elementTarget });
		}
	}

	@Override
	public void setCollectionTable(ITable table) {
		if (isCollection()) {
			collectionTable = table;
			Object tableTarget = Util.invokeMethod(
					table, 
					"getTarget", 
					new Class[] {}, 
					new Object[] {});
			Util.invokeMethod(
					getTarget(), 
					"setCollectionTable", 
					new Class[] { getTableClass() }, 
					new Object[] { tableTarget });
		}
	}

	@Override
	public void setTable(ITable table) {
		if (isSimpleValue()) {
			Object tableTarget = Util.invokeMethod(
					table, 
					"getTarget", 
					new Class[] {}, 
					new Object[] {});
			Util.invokeMethod(
					getTarget(), 
					"setTable", 
					new Class[] { getTableClass() }, 
					new Object[] { tableTarget });
		}
	}

	@Override
	public boolean isList() {
		return getListClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public void setIndex(IValue value) {
		Object valueTarget = Util.invokeMethod(
				value, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"setIndex", 
				new Class[] { getValueClass() }, 
				new Object[] { valueTarget });
	}

	@Override
	public void setTypeName(String name) {
		if (isSimpleValue()) {
			Util.invokeMethod(
					getTarget(), 
					"setTypeName", 
					new Class[] { String.class }, 
					new Object[] { name });
		}
	}

	@Override
	public String getComponentClassName() {
		String result = null;
		if (isComponent()) {
			result = (String)Util.invokeMethod(
					getTarget(), 
					"getComponentClassName", 
					new Class[] {}, 
					new Object[] {});
		}
		return result;
	}

	@Override
	public Iterator<IColumn> getColumnIterator() {
		if (columns == null) {
			initializeColumns();
		}
		return columns.iterator();
	}
	
	@Override
	public Boolean isTypeSpecified() {
		Boolean result = null;
		if (isSimpleValue()) {
			result = (Boolean)Util.invokeMethod(
					getTarget(), 
					"isTypeSpecified", 
					new Class[] {}, 
					new Object[] {});
		}
		return result; 
	}
	
	@Override
	public String toString() {
		return getTarget().toString();
	}

	@Override
	public ITable getCollectionTable() {
		if (isCollection() && collectionTable == null) {
			Object ct = Util.invokeMethod(
					getTarget(), 
					"getCollectionTable", 
					new Class[] {}, 
					new Object[] {});
			if (ct != null) {
				collectionTable = getFacadeFactory().createTable(ct);
			}
		}
		return collectionTable;
	}

	@Override
	public IValue getKey() {
		if (key == null && isCollection()) {
			Object targetKey = Util.invokeMethod(
					getTarget(), 
					"getKey", 
					new Class[] {}, 
					new Object[] {});
			if (targetKey != null) {
				key = getFacadeFactory().createValue(targetKey);
			}
		}
		return key;
	}

	public boolean isDependantValue() {
		return getDependantValueClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isAny() {
		return getAnyClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isSet() {
		return getSetClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public IValue getIndex() {
		if (index == null && isList()) {
			Object targetIndex = Util.invokeMethod(
					getTarget(), 
					"getIndex", 
					new Class[] {}, 
					new Object[] {});
			if (targetIndex != null) {
				index = getFacadeFactory().createValue(targetIndex);
			}
		}
		return index;
	}

	@Override
	public String getElementClassName() {
		String result = null;
		if (isArray()) {
			result = (String)Util.invokeMethod(
					getTarget(), 
					"getElementClassName", 
					new Class[] {}, 
					new Object[] {});
		}
		return result;
	}

	@Override
	public boolean isArray() {
		return getArrayClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isPrimitiveArray() {
		return getPrimitiveArrayClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public String getTypeName() {
		String result = null;
		if (isSimpleValue())  {
			result = (String)Util.invokeMethod(
					getTarget(), 
					"getTypeName", 
					new Class[] {}, 
					new Object[] {});
		}
		return result;
	}

	@Override
	public boolean isIdentifierBag() {
		return getIdentifierBagClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public boolean isBag() {
		return getBagClass().isAssignableFrom(getTarget().getClass());
	}

	@Override
	public String getReferencedEntityName() {
		String result = null;
		if (isOneToMany() || isToOne()) {
			result = (String)Util.invokeMethod(
					getTable(), 
					"getReferencedEntityName", 
					new Class[] {}, 
					new Object[] {});
		}
		return result;
	}

	@Override
	public String getEntityName() {
		String result = null;
		if (isOneToOne()) {
		result = (String)Util.invokeMethod(
				getTable(), 
				"getEntityName", 
				new Class[] {}, 
				new Object[] {});
		}
		return result;
	}

	@Override
	public Iterator<IProperty> getPropertyIterator() {
		if (properties == null) {
			initializeProperties();
		}
		return properties.iterator();
	}
	
	@Override
	public void addColumn(IColumn column) {
		Object columnTarget = Util.invokeMethod(
				column, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"addColumn", 
				new Class[] { getColumnClass() }, 
				new Object[] { columnTarget });
	}

	@Override
	public void setTypeParameters(Properties typeParameters) {
		Util.invokeMethod(
				getTarget(), 
				"setTypeParameters", 
				new Class[] { Properties.class }, 
				new Object[] { typeParameters });
	}

	@Override
	public String getForeignKeyName() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getForeignKeyName", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public IPersistentClass getOwner() {
		if (owner == null) {
			Object targetOwner = Util.invokeMethod(
					getTarget(), 
					"getOwner", 
					new Class[] {}, 
					new Object[] {});
			if (targetOwner != null) {
				owner = getFacadeFactory().createPersistentClass(targetOwner);
			}
		}
		return owner;
	}

	@Override
	public IValue getElement() {
		IValue result = null;
		Object targetElement = Util.invokeMethod(
				getTarget(), 
				"getElement", 
				new Class[] {}, 
				new Object[] {});
		if (targetElement != null) {
			result = getFacadeFactory().createValue(targetElement);
		}
		return result;
	}

	@Override
	public String getParentProperty() {
		return (String)Util.invokeMethod(
				getTarget(), 
				"getParentProperty", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public void setElementClassName(String name) {
		Util.invokeMethod(
				getTarget(), 
				"setElementClassName", 
				new Class[] { String.class }, 
				new Object[] { name });
	}

	@Override
	public void setKey(IValue keyValue) {
		Object keyValueTarget = Util.invokeMethod(
				keyValue, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"setKey", 
				new Class[] { getKeyValueClass() }, 
				new Object[] { keyValueTarget });
	}

	@Override
	public void setFetchModeJoin() {
		if (isCollection() || isToOne()) {
			Object fetchModeJoin = Util.getFieldValue(
					getFetchModeClass(),
					"JOIN", 
					null);
			Util.invokeMethod(
					getTarget(), 
					"setFetchMode", 
					new Class[] { getFetchModeClass() }, 
					new Object[] { fetchModeJoin });
		}
	}

	@Override
	public boolean isInverse() {
		return (boolean)Util.invokeMethod(
				getTarget(), 
				"isInverse", 
				new Class[] {}, 
				new Object[] {});
	}

	@Override
	public IPersistentClass getAssociatedClass() {
		if (associatedClass == null) {
			Object targetAssociatedClass = Util.invokeMethod(
					getTarget(), 
					"getAssociatedClass", 
					new Class[] {}, 
					new Object[] {});
			if (targetAssociatedClass != null) {
				associatedClass = 
						getFacadeFactory().createPersistentClass(
								targetAssociatedClass);
			}
		}
		return associatedClass;
	}

	@Override
	public void setLazy(boolean b) {
		Util.invokeMethod(
				getTarget(), 
				"setLazy", 
				new Class[] { boolean.class }, 
				new Object[] { b });
	}

	@Override
	public void setRole(String role) {
		Util.invokeMethod(
				getTarget(), 
				"setRole", 
				new Class[] { String.class }, 
				new Object[] { role });
	}

	@Override
	public void setReferencedEntityName(String name) {
		if (isToOne() || isOneToMany()) {
			Util.invokeMethod(
					getTarget(), 
					"setReferencedEntityName", 
					new Class[] { String.class }, 
					new Object[] { name });
		}
	}

	@Override
	public void setAssociatedClass(IPersistentClass persistentClass) {
		Object persistentClassTarget = Util.invokeMethod(
				persistentClass, 
				"getTarget", 
				new Class[] {}, 
				new Object[] {});
		Util.invokeMethod(
				getTarget(), 
				"setAssociatedClass", 
				new Class[] { getPersistentClassClass() }, 
				new Object[] { persistentClassTarget });
	}
	
	protected Class<?> getCollectionClass() {
		return Util.getClass(collectionClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getOneToManyClass() {
		return Util.getClass(oneToManyClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getManyToOneClass() {
		return Util.getClass(manyToOneClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getOneToOneClass() {
		return Util.getClass(oneToOneClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getMapClass() {
		return Util.getClass(mapClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getComponentClass() {
		return Util.getClass(componentClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getToOneClass() {
		return Util.getClass(toOneClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getValueClass() {
		return Util.getClass(valueClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getTableClass() {
		return Util.getClass(tableClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getListClass() {
		return Util.getClass(listClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getColumnClass() {
		return Util.getClass(columnClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getDependantValueClass() {
		return Util.getClass(dependantValueClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getAnyClass() {
		return Util.getClass(anyClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getSetClass() {
		return Util.getClass(setClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getArrayClass() {
		return Util.getClass(arrayClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getPrimitiveArrayClass() {
		return Util.getClass(primitiveArrayClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getIdentifierBagClass() {
		return Util.getClass(identifierBagClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getBagClass() {
		return Util.getClass(bagClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getKeyValueClass() {
		return Util.getClass(keyValueClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getFetchModeClass() {
		return Util.getClass(fetchModeClassName(), getFacadeFactoryClassLoader());
	}
	
	protected Class<?> getPersistentClassClass() {
		return Util.getClass(persistentClassClassName(), getFacadeFactoryClassLoader());
	}
	
	protected String collectionClassName() {
		return "org.hibernate.mapping.Collection";
	}
	
	protected String oneToManyClassName() {
		return "org.hibernate.mapping.OneToMany";
	}

	protected String manyToOneClassName() {
		return "org.hibernate.mapping.ManyToOne";
	}

	protected String oneToOneClassName() {
		return "org.hibernate.mapping.OneToOne";
	}

	protected String mapClassName() {
		return "org.hibernate.mapping.Map";
	}

	protected String componentClassName() {
		return "org.hibernate.mapping.Component";
	}

	protected String toOneClassName() {
		return "org.hibernate.mapping.ToOne";
	}

	protected String valueClassName() {
		return "org.hibernate.mapping.Value";
	}

	protected String tableClassName() {
		return "org.hibernate.mapping.Table";
	}

	protected String listClassName() {
		return "org.hibernate.mapping.List";
	}

	protected String columnClassName() {
		return "org.hibernate.mapping.Column";
	}

	protected String dependantValueClassName() {
		return "org.hibernate.mapping.DependantValue";
	}

	protected String anyClassName() {
		return "org.hibernate.mapping.Any";
	}

	protected String setClassName() {
		return "org.hibernate.mapping.Set";
	}

	protected String arrayClassName() {
		return "org.hibernate.mapping.Array";
	}

	protected String primitiveArrayClassName() {
		return "org.hibernate.mapping.PrimitiveArray";
	}

	protected String identifierBagClassName() {
		return "org.hibernate.mapping.IdentifierBag";
	}

	protected String bagClassName() {
		return "org.hibernate.mapping.Bag";
	}
	
	protected String keyValueClassName() {
		return "org.hibernate.mapping.KeyValue";
	}
	
	protected String fetchModeClassName() {
		return "org.hibernate.FetchMode";
	}
	
	protected String persistentClassClassName() {
		return "org.hibernate.mapping.PersistentClass";
	}

	private void initializeColumns() {
		columns = new HashSet<IColumn>();
		Iterator<?> iterator = (Iterator<?>)Util.invokeMethod(
				getTarget(), 
				"getColumnIterator", 
				new Class[] {}, 
				new Object[] {});
		while (iterator.hasNext()) {
			Object object = iterator.next();
			if (getColumnClass().isAssignableFrom(object.getClass())) {
				columns.add(getFacadeFactory().createColumn(object));
			}
		}
	}

	private void initializeProperties() {
		properties = new HashSet<IProperty>();
		Iterator<?> iterator = (Iterator<?>)Util.invokeMethod(
				getTarget(), 
				"getPropertyIterator", 
				new Class[] {}, 
				new Object[] {});
		while (iterator.hasNext()) {
			properties.add(getFacadeFactory().createProperty(iterator.next()));
		}
	}

}
