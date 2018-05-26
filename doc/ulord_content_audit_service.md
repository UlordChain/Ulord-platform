#Content Audit Service
[中文](ulord_content_audit_service_zh.md)

## Architecture

![Ulord Content Audit Arch](../images/Ulord-ContentAudit-Arch.png)



Ulord content audit is divided into two parts, one part in the enterprise end, the main auxiliary enterprise to complete the content audit, another part in the Ulord platform, the main completion of the content audit feature information synchronization and active discovery function.

The Ulrod enterprise end is divided into two parts, a content audit service running in a service way, which provides the content audit function service, while maintaining the connection with the server, so as to synchronize the design features of the whole network content. The other is in SDK form, supporting multi lingual.

SDK, the implementation and content audit service docking.

Content audit services run two advantages in the enterprise end:
1. the content of the original is only transferred at the enterprise side, and there is no leakage of content.
2. the transmission of content is completed in the intranet and has high throughput processing capability.

