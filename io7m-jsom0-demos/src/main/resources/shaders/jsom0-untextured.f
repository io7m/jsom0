#version 110

varying vec3 f_normal;
varying vec2 f_uv;
varying vec3 f_position_VS;
varying vec3 f_light_direction_VS;

uniform vec3  material_diffuse;
uniform vec3  material_ambient;
uniform vec4  material_specular;
uniform float material_shininess;

void
main (void)
{
  vec3 N = normalize(f_normal);
  vec3 V = normalize(f_position_VS);
  vec3 R = reflect(V, N);
  vec3 L = normalize(f_light_direction_VS);

  vec4 ambient = vec4(material_ambient, 1.0);
  vec4 diffuse = vec4(material_diffuse, 1.0) * max(dot(L, N), 0.0);
  vec4 specular = material_specular * pow(max(dot(R, L), 0.0), material_shininess);

  gl_FragColor = ambient + diffuse + specular;
}
