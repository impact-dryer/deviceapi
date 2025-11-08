package com.impactdryer.deviceapi.devicemanagment.infrastructure;

import com.impactdryer.deviceapi.devicemanagment.domain.DeviceType;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.proxy.HibernateProxy;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "devices", uniqueConstraints = {@UniqueConstraint(name = "uk_devices_mac", columnNames = "mac_address")}, indexes = {@Index(name = "idx_devices_type", columnList = "device_type"), @Index(name = "idx_devices_uplink", columnList = "uplink_id")})
@Data
public class DeviceEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Version
  @Column(name = "version")
  private Long version;

  @Enumerated(EnumType.STRING)
  @Column(name = "device_type", nullable = false, length = 32)
  private DeviceType deviceType;

  @Column(name = "mac_address", nullable = false, length = 17, updatable = false)
  private String macAddress;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "uplink_id")
  private DeviceEntity uplink;

  @OneToMany(mappedBy = "uplink", fetch = FetchType.LAZY)
  private Set<DeviceEntity> downlinks = new HashSet<>();

  protected DeviceEntity() {
    // for JPA
  }

  public DeviceEntity(String macAddress, DeviceType deviceType) {
    this.macAddress = Objects.requireNonNull(macAddress, "macAddress must not be null");
    this.deviceType = Objects.requireNonNull(deviceType, "deviceType must not be null");
  }

  public static DeviceEntity withUplink(String macAddress, DeviceType deviceType, DeviceEntity uplink) {
    DeviceEntity d = new DeviceEntity(macAddress, deviceType);
    d.attachToUplink(uplink);
    return d;
  }


  public void attachToUplink(DeviceEntity uplink) {
    this.uplink = uplink;
  }

  public void detachUplink() {
    this.uplink = null;
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    DeviceEntity deviceEntity = (DeviceEntity) o;
    return getId() != null && Objects.equals(getId(), deviceEntity.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}
