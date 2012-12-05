#version 110

varying vec3 f_normal;
varying vec2 f_uv;
varying vec3 f_position_VS;
varying vec3 f_light_direction_VS;

uniform sampler2D texture;
uniform float     texture_alpha;
uniform vec3      material_diffuse;
uniform vec3      material_ambient;
uniform vec4      material_specular;
uniform float     material_shininess;

void
main (void)
{
  vec4 texture_rgb = texture2D(texture, f_normal.xy);
  texture_rgb      = vec4(texture_rgb.rgb * texture_alpha, 1.0);
  
  vec4 in_diffuse = texture_rgb + vec4(material_diffuse * (1.0 - texture_alpha), 1.0);

  vec3 N = normalize(f_normal);
  vec3 V = normalize(f_position_VS);
  vec3 R = reflect(V, N);
  vec3 L = normalize(f_light_direction_VS);

  vec4 ambient = vec4(material_ambient, 1.0);
  vec4 diffuse = in_diffuse * max(dot(L, N), 0.0);
  vec4 specular = material_specular * pow(max(dot(R, L), 0.0), material_shininess);

  gl_FragColor = ambient + diffuse + specular;
}
