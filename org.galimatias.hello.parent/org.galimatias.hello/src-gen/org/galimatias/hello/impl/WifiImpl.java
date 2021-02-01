/**
 * generated by Xtext 2.18.0
 */
package org.galimatias.hello.impl;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.MinimalEObjectImpl;

import org.galimatias.hello.HelloPackage;
import org.galimatias.hello.Wifi;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Wifi</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * </p>
 * <ul>
 *   <li>{@link org.galimatias.hello.impl.WifiImpl#getSsid <em>Ssid</em>}</li>
 *   <li>{@link org.galimatias.hello.impl.WifiImpl#getPassword <em>Password</em>}</li>
 * </ul>
 *
 * @generated
 */
public class WifiImpl extends MinimalEObjectImpl.Container implements Wifi
{
  /**
   * The default value of the '{@link #getSsid() <em>Ssid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSsid()
   * @generated
   * @ordered
   */
  protected static final String SSID_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getSsid() <em>Ssid</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getSsid()
   * @generated
   * @ordered
   */
  protected String ssid = SSID_EDEFAULT;

  /**
   * The default value of the '{@link #getPassword() <em>Password</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPassword()
   * @generated
   * @ordered
   */
  protected static final String PASSWORD_EDEFAULT = null;

  /**
   * The cached value of the '{@link #getPassword() <em>Password</em>}' attribute.
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @see #getPassword()
   * @generated
   * @ordered
   */
  protected String password = PASSWORD_EDEFAULT;

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  protected WifiImpl()
  {
    super();
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  protected EClass eStaticClass()
  {
    return HelloPackage.Literals.WIFI;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getSsid()
  {
    return ssid;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setSsid(String newSsid)
  {
    String oldSsid = ssid;
    ssid = newSsid;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, HelloPackage.WIFI__SSID, oldSsid, ssid));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public String getPassword()
  {
    return password;
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  public void setPassword(String newPassword)
  {
    String oldPassword = password;
    password = newPassword;
    if (eNotificationRequired())
      eNotify(new ENotificationImpl(this, Notification.SET, HelloPackage.WIFI__PASSWORD, oldPassword, password));
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public Object eGet(int featureID, boolean resolve, boolean coreType)
  {
    switch (featureID)
    {
      case HelloPackage.WIFI__SSID:
        return getSsid();
      case HelloPackage.WIFI__PASSWORD:
        return getPassword();
    }
    return super.eGet(featureID, resolve, coreType);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eSet(int featureID, Object newValue)
  {
    switch (featureID)
    {
      case HelloPackage.WIFI__SSID:
        setSsid((String)newValue);
        return;
      case HelloPackage.WIFI__PASSWORD:
        setPassword((String)newValue);
        return;
    }
    super.eSet(featureID, newValue);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public void eUnset(int featureID)
  {
    switch (featureID)
    {
      case HelloPackage.WIFI__SSID:
        setSsid(SSID_EDEFAULT);
        return;
      case HelloPackage.WIFI__PASSWORD:
        setPassword(PASSWORD_EDEFAULT);
        return;
    }
    super.eUnset(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public boolean eIsSet(int featureID)
  {
    switch (featureID)
    {
      case HelloPackage.WIFI__SSID:
        return SSID_EDEFAULT == null ? ssid != null : !SSID_EDEFAULT.equals(ssid);
      case HelloPackage.WIFI__PASSWORD:
        return PASSWORD_EDEFAULT == null ? password != null : !PASSWORD_EDEFAULT.equals(password);
    }
    return super.eIsSet(featureID);
  }

  /**
   * <!-- begin-user-doc -->
   * <!-- end-user-doc -->
   * @generated
   */
  @Override
  public String toString()
  {
    if (eIsProxy()) return super.toString();

    StringBuffer result = new StringBuffer(super.toString());
    result.append(" (ssid: ");
    result.append(ssid);
    result.append(", password: ");
    result.append(password);
    result.append(')');
    return result.toString();
  }

} //WifiImpl
